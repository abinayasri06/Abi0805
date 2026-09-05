import pytest
from src.triage.validator import sanitize_patient_input, parse_followup_answer, ValidationError
from src.models.schemas import PatientInput


def test_empty_input_validation():
    """Test 14: Empty patient input triggers validation error."""
    with pytest.raises(ValidationError):
        sanitize_patient_input("")

    with pytest.raises(ValidationError):
        sanitize_patient_input("   ")


def test_too_short_input_validation():
    """Input below minimum characters triggers validation error."""
    with pytest.raises(ValidationError):
        sanitize_patient_input("ab")


def test_very_long_input_validation():
    """Overly long input (>2000 chars) is rejected."""
    long_text = "symptoms " * 300  # > 2400 chars
    with pytest.raises(ValidationError):
        sanitize_patient_input(long_text)


def test_unknown_answer_parsing():
    """
    Test 8: If the patient says 'I don't know', store 'unknown', NOT False, no, or negative.
    This distinction is critical.
    """
    assert parse_followup_answer("I don't know") == "unknown"
    assert parse_followup_answer("i dont know") == "unknown"
    assert parse_followup_answer("Not sure") == "unknown"
    assert parse_followup_answer("unsure") == "unknown"
    assert parse_followup_answer("unknown") == "unknown"
    assert parse_followup_answer("no idea") == "unknown"

    # Crucial assertion: "unknown" MUST NOT equal False or evaluate to negative boolean
    val = parse_followup_answer("I don't know")
    assert val != False
    assert val == "unknown"


def test_affirmative_and_negative_answer_parsing():
    """Affirmative and negative values are parsed cleanly."""
    assert parse_followup_answer("Yes") is True
    assert parse_followup_answer("yes, currently present") is True
    assert parse_followup_answer("No") is False
    assert parse_followup_answer("no, it has stopped") is False


def test_pydantic_patient_input_validation():
    """Pydantic model rejects blank or whitespace text."""
    with pytest.raises(ValueError):
        PatientInput(text="   ")
