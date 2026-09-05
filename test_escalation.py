import pytest
from unittest.mock import patch, MagicMock
from src.models.schemas import ComplaintCategory
from src.triage.escalation import evaluate_escalation
from src.triage.rule_engine import RuleEngine
from src.llm.extractor import extract_patient_facts
from src.llm.gemini_client import GeminiAPIError


@pytest.fixture
def rule_engine():
    return RuleEngine()


def test_ambiguous_description_escalation(rule_engine):
    """Test 7: Ambiguous / unclear description escalates to Human Review."""
    result = rule_engine.evaluate(
        assessment_id="TEST-AMBIGUOUS-1",
        complaint_category=ComplaintCategory.UNCLEAR,
        facts={},
        unknown_fields=[]
    )
    assert result.escalated is True
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert "could not confidently determine" in result.reasoning
    assert result.rule_id == "UNCLEAR-ESC-001"


def test_unknown_complaint_escalation(rule_engine):
    """Test 12: Unknown / unsupported complaint (e.g. earache, dental) maps to UNCLEAR and escalates to human review."""
    facts = extract_patient_facts("I have had severe ringing and pain in my left ear since yesterday.")
    assert facts.complaint_category == ComplaintCategory.UNCLEAR
    
    result = rule_engine.evaluate(
        assessment_id="TEST-UNKNOWN-COMPLAINT-1",
        complaint_category=facts.complaint_category,
        facts=facts.facts,
        unknown_fields=facts.unknown
    )
    assert result.escalated is True
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert result.rule_id == "UNCLEAR-ESC-001"
    assert "could not confidently determine" in result.reasoning


def test_high_risk_escalation(rule_engine):
    """Test 9: High-risk case (acute chest pain + dyspnea) triggers human review escalation."""
    facts = {
        "chest_pain_present": True,
        "breathing_difficulty": True,
        "current_status": "present",
        "onset": "this morning",
        "severity": "severe"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-HIGHRISK-1",
        complaint_category=ComplaintCategory.CHEST_PAIN,
        facts=facts,
        unknown_fields=[]
    )
    assert result.escalated is True
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert result.rule_id == "CP-001"


def test_high_risk_breathing_difficulty_speech_impairment(rule_engine):
    """Severe dyspnea with inability to speak full sentences escalates."""
    facts = {
        "breathing_difficulty_present": True,
        "can_speak_full_sentences": False,
        "onset": "sudden",
        "current_status": "present",
        "severity": "severe"
    }
    result = rule_engine.evaluate(
        assessment_id="TEST-BD-HIGH-1",
        complaint_category=ComplaintCategory.BREATHING_DIFFICULTY,
        facts=facts,
        unknown_fields=[]
    )
    assert result.escalated is True
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert result.rule_id == "BD-001"


def test_gemini_failure_escalation(rule_engine):
    """Test 10: Gemini failure triggers safe human escalation without crash."""
    result = rule_engine.evaluate(
        assessment_id="TEST-FAIL-1",
        complaint_category=ComplaintCategory.CHEST_PAIN,
        facts={},
        unknown_fields=[],
        is_model_failure=True
    )
    assert result.escalated is True
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert "AI-assisted language processing is temporarily unavailable" in result.reasoning
    assert result.rule_id == "SYS-ESC-001"


def test_invalid_gemini_response_fallback():
    """Test 11: Invalid Gemini JSON falls back safely without unhandled exception."""
    mock_client = MagicMock()
    mock_client.is_available = True
    # Simulate invalid JSON from Gemini
    mock_client.generate_json.return_value = "This is not valid JSON at all!"

    with patch("src.llm.extractor.get_gemini_client", return_value=mock_client):
        # Should catch error, fallback to deterministic parser, and not raise exception
        facts = extract_patient_facts("My chest hurts since this morning.")
        assert facts is not None
        assert facts.complaint_category in [ComplaintCategory.CHEST_PAIN, ComplaintCategory.UNCLEAR]


def test_missing_rule_escalation(rule_engine):
    """Test 13: Category with no applicable rules safely escalates."""
    # Create an artificial engine with empty rules
    empty_engine = RuleEngine()
    empty_engine.rules = []

    result = empty_engine.evaluate(
        assessment_id="TEST-NORULE-1",
        complaint_category=ComplaintCategory.FEVER,
        facts={"duration_days": "3_days_or_less"},
        unknown_fields=[]
    )
    assert result.escalated is True
    assert result.urgency == "HUMAN REVIEW REQUIRED"
    assert "RULE-MISSING-ESC" in result.rule_id
