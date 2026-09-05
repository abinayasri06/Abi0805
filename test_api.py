import pytest
from fastapi.testclient import TestClient
from src.api import app
from src.database.db import init_db

client = TestClient(app)


@pytest.fixture(autouse=True)
def setup_database():
    """Ensure DB is initialized before tests."""
    init_db()


def test_health_check():
    """Verify GET /api/health returns {'status': 'ok'}."""
    response = client.get("/api/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_serve_frontend_index():
    """Verify GET / serves HTML index page."""
    response = client.get("/")
    assert response.status_code == 200
    assert "TriageAI" in response.text


def test_get_demo_cases():
    """Verify GET /api/demo-cases returns preset scenarios."""
    response = client.get("/api/demo-cases")
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)
    assert len(data) >= 5


def test_intake_empty_input_returns_400():
    """POST /api/intake with empty input returns 400."""
    response = client.post("/api/intake", json={"text": "   "})
    assert response.status_code == 400


def test_full_intake_and_assessment_flow():
    """Verify end-to-end patient journey from intake to triage note."""
    # 1. Intake
    intake_resp = client.post("/api/intake", json={
        "text": "I have had a fever since yesterday and feel tired."
    })
    assert intake_resp.status_code == 200
    intake_data = intake_resp.json()
    assert "assessment_id" in intake_data
    assert intake_data["complaint_category"] == "FEVER"
    assessment_id = intake_data["assessment_id"]

    # 2. Submit Follow-up answers
    followup_resp = client.post("/api/followup", json={
        "assessment_id": assessment_id,
        "answers": [
            {"field": "stiff_neck_or_confusion", "answer": "No"},
            {"field": "duration_days", "answer": "3 days or less"},
            {"field": "temperature_reported", "answer": "38.5 C"}
        ]
    })
    assert followup_resp.status_code == 200
    followup_data = followup_resp.json()
    assert followup_data["updated_facts"]["stiff_neck_or_confusion"] is False

    # 3. Assess
    assess_resp = client.post("/api/assess", json={"assessment_id": assessment_id})
    assert assess_resp.status_code == 200
    assess_data = assess_resp.json()
    assert "triage_result" in assess_data
    assert "triage_note" in assess_data
    assert assess_data["triage_result"]["rule_id"] == "FEV-003"
    assert assess_data["triage_result"]["urgency"] == "NON_URGENT"

    # 4. Fetch stored assessment
    get_resp = client.get(f"/api/assessment/{assessment_id}")
    assert get_resp.status_code == 200
    stored_data = get_resp.json()
    assert stored_data["assessment_id"] == assessment_id
    assert stored_data["matched_rule_id"] == "FEV-003"


def test_followup_unknown_answer_preserves_unknown():
    """Verify answering 'I don't know' records as unknown and escalates in assessment."""
    intake_resp = client.post("/api/intake", json={
        "text": "I have chest discomfort since yesterday."
    })
    assessment_id = intake_resp.json()["assessment_id"]

    # Answering 'I don't know'
    followup_resp = client.post("/api/followup", json={
        "assessment_id": assessment_id,
        "answers": [
            {"field": "breathing_difficulty", "answer": "I don't know"}
        ]
    })
    assert followup_resp.status_code == 200
    data = followup_resp.json()
    assert data["updated_facts"]["breathing_difficulty"] == "unknown"

    # Assessment must escalate because breathing_difficulty is critical & unknown
    assess_resp = client.post("/api/assess", json={"assessment_id": assessment_id})
    assert assess_resp.status_code == 200
    res = assess_resp.json()["triage_result"]
    assert res["escalated"] is True
    assert res["urgency"] == "HUMAN REVIEW REQUIRED"
