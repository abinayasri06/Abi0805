import pytest
from src.models.schemas import ComplaintCategory, TriageResult
from src.triage.rule_engine import RuleEngine
from src.notes.generator import generate_triage_note


@pytest.fixture
def rule_engine():
    return RuleEngine()


def test_fever_normal_case(rule_engine):
    """Test 1: Fever normal case (<3 days, no stiff neck) -> FEV-003 Routine."""
    facts = {
        "stiff_neck_or_confusion": False,
        "duration_days": "3_days_or_less",
        "temperature_reported": "38.2 C",
        "current_symptoms": "mild malaise"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-FEV-1",
        complaint_category=ComplaintCategory.FEVER,
        facts=facts,
        unknown_fields=[]
    )
    assert result.rule_id == "FEV-003"
    assert result.urgency == "NON_URGENT"
    assert "Primary Care" in result.department
    assert result.escalated is False
    assert result.rule_id is not None
    assert len(result.evidence_text) > 0


def test_injury_urgent_case(rule_engine):
    """Test 2: Injury case (unable to bear weight, no severe deformity) -> INJ-002 Urgent."""
    facts = {
        "severe_deformity_or_bleeding": False,
        "can_bear_weight": False,
        "location": "ankle",
        "mechanism": "fell off bicycle"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-INJ-1",
        complaint_category=ComplaintCategory.INJURY,
        facts=facts,
        unknown_fields=[]
    )
    assert result.rule_id == "INJ-002"
    assert result.urgency == "URGENT"
    assert "Orthopedics" in result.department
    assert result.escalated is False
    assert result.rule_id is not None


def test_chest_pain_emergency_case(rule_engine):
    """Test 3: Chest pain emergency case (chest pain present + breathing difficulty) -> CP-001."""
    facts = {
        "chest_pain_present": True,
        "breathing_difficulty": True,
        "onset": "this morning",
        "current_status": "present",
        "severity": "severe"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-CP-1",
        complaint_category=ComplaintCategory.CHEST_PAIN,
        facts=facts,
        unknown_fields=[]
    )
    assert result.rule_id == "CP-001"
    # High-risk chest pain triggers Human Review Required safety escalation
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert "Emergency" in result.department
    assert result.escalated is True
    assert result.rule_id is not None
    assert "CP-001" in result.evidence_text or "CP-001" in result.rule_id


def test_breathing_difficulty_urgent_case(rule_engine):
    """Test 4: Breathing difficulty urgent case (can speak full sentences) -> BD-002."""
    facts = {
        "breathing_difficulty_present": True,
        "can_speak_full_sentences": True,
        "onset": "gradual",
        "current_status": "present",
        "severity": "moderate"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-BD-1",
        complaint_category=ComplaintCategory.BREATHING_DIFFICULTY,
        facts=facts,
        unknown_fields=[]
    )
    assert result.rule_id == "BD-002"
    assert result.urgency == "URGENT"
    assert "Pulmonology" in result.department
    assert result.escalated is False


def test_abdominal_pain_urgent_case(rule_engine):
    """Test 5: Abdominal pain urgent case (worsening, no vomiting blood) -> AP-002."""
    facts = {
        "vomiting_blood_or_rigid_abdomen": False,
        "is_worsening": True,
        "location": "lower right quadrant",
        "onset_and_duration": "since morning",
        "severity_score": "7"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-AP-1",
        complaint_category=ComplaintCategory.ABDOMINAL_PAIN,
        facts=facts,
        unknown_fields=[]
    )
    assert result.rule_id == "AP-002"
    assert result.urgency == "URGENT"
    assert "Surgery" in result.department or "Abdominal" in result.department
    assert result.escalated is False


def test_missing_information_causes_escalation(rule_engine):
    """Test 6: Missing information does not guess; sets escalate=True."""
    facts = {
        "chest_pain_present": True
        # breathing_difficulty is missing / unknown
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-MISSING-1",
        complaint_category=ComplaintCategory.CHEST_PAIN,
        facts=facts,
        unknown_fields=["breathing_difficulty", "severity"]
    )
    assert result.escalated is True
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert "Required information remains unknown" in result.reasoning


def test_rule_citation_exists_for_every_recommendation(rule_engine):
    """Test 15: Rule citation exists for every recommendation."""
    for category in [
        ComplaintCategory.FEVER,
        ComplaintCategory.INJURY,
        ComplaintCategory.CHEST_PAIN,
        ComplaintCategory.BREATHING_DIFFICULTY,
        ComplaintCategory.ABDOMINAL_PAIN
    ]:
        result = rule_engine.evaluate(
            assessment_id=f"TEST-CITE-{category.value}",
            complaint_category=category,
            facts={},
            unknown_fields=["field1"]
        )
        assert result.rule_id is not None
        assert len(result.rule_id) > 0
        assert result.evidence_text is not None
        assert len(result.evidence_text) > 0


def test_no_diagnosis_in_triage_note(rule_engine):
    """Ensure final triage note strictly forbids disease diagnosis."""
    facts = {
        "stiff_neck_or_confusion": False,
        "duration_days": "3_days_or_less"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-NOTE-1",
        complaint_category=ComplaintCategory.FEVER,
        facts=facts,
        unknown_fields=[]
    )
    note = generate_triage_note(
        assessment_id="TEST-NOTE-1",
        raw_complaint="I have had a mild fever since yesterday.",
        complaint_category=ComplaintCategory.FEVER,
        initial_facts=facts,
        followup_answers=[],
        still_unknown=[],
        triage_result=result
    )
    assert "Diagnosis:\nNot provided" in note.formatted_text or "Diagnosis:\nNot provided (decision support only)" in note.formatted_text
    assert "heart attack" not in note.formatted_text.lower()
    assert "pneumonia" not in note.formatted_text.lower()
    assert "appendicitis" not in note.formatted_text.lower()
