from enum import Enum
from typing import Dict, Any, List, Optional
from pydantic import BaseModel, Field, field_validator


class ComplaintCategory(str, Enum):
    FEVER = "FEVER"
    INJURY = "INJURY"
    CHEST_PAIN = "CHEST_PAIN"
    BREATHING_DIFFICULTY = "BREATHING_DIFFICULTY"
    ABDOMINAL_PAIN = "ABDOMINAL_PAIN"
    UNCLEAR = "UNCLEAR"


class UrgencyLevel(str, Enum):
    EMERGENCY = "EMERGENCY"
    URGENT = "URGENT"
    NON_URGENT = "NON_URGENT"
    HUMAN_REVIEW_REQUIRED = "HUMAN REVIEW REQUIRED"


class PatientInput(BaseModel):
    text: str = Field(..., min_length=1, max_length=2000, description="Patient description in everyday language")
    assessment_id: Optional[str] = Field(None, description="Optional existing assessment ID")

    @field_validator("text")
    @classmethod
    def validate_text(cls, v: str) -> str:
        cleaned = v.strip()
        if not cleaned:
            raise ValueError("Patient description cannot be empty or whitespace only")
        return cleaned


class StructuredPatientFacts(BaseModel):
    complaint_category: ComplaintCategory
    patient_report: str
    facts: Dict[str, Any] = Field(default_factory=dict)
    unknown: List[str] = Field(default_factory=list)
    confidence_score: float = 1.0
    unclear_reason: Optional[str] = None


class FollowUpQuestion(BaseModel):
    id: str
    field: str
    question: str
    options: List[str] = Field(default_factory=list)
    type: str = "text"


class FollowUpAnswer(BaseModel):
    field: str
    answer: str

    @field_validator("answer")
    @classmethod
    def sanitize_answer(cls, v: str) -> str:
        return v.strip()


class FollowUpSubmission(BaseModel):
    assessment_id: str
    answers: List[FollowUpAnswer]


class Rule(BaseModel):
    rule_id: str
    complaint: ComplaintCategory
    required_information: List[str]
    conditions: Dict[str, Any]
    urgency: str
    department: str
    action: str
    reasoning: str
    escalation_condition: str
    evidence_text: str


class RuleMatch(BaseModel):
    rule_id: str
    matched: bool
    matched_conditions: Dict[str, Any] = Field(default_factory=dict)
    unmatched_conditions: Dict[str, Any] = Field(default_factory=dict)
    missing_facts: List[str] = Field(default_factory=list)


class EscalationResult(BaseModel):
    escalate: bool
    reason: Optional[str] = None
    urgency: str = "HUMAN REVIEW REQUIRED"
    recommended_department: Optional[str] = "Emergency / Acute Clinical Assessment"


class TriageResult(BaseModel):
    assessment_id: str
    urgency: str
    department: str
    rule_id: str
    matched_conditions: Dict[str, Any] = Field(default_factory=dict)
    reasoning: str
    escalated: bool = False
    escalation_reason: Optional[str] = None
    evidence_text: str = ""
    retrieved_rules: List[Dict[str, Any]] = Field(default_factory=list)


class TriageNote(BaseModel):
    assessment_id: str
    complaint: str
    recommended_urgency: str
    recommended_department: str
    rule_id: str
    why_recommendation: str
    patient_reported: List[str] = Field(default_factory=list)
    followup_established: List[str] = Field(default_factory=list)
    still_unknown: List[str] = Field(default_factory=list)
    safety_status: str
    diagnosis: str = "Not provided (decision support only)"
    formatted_text: str


class AssessmentRecord(BaseModel):
    assessment_id: str
    created_at: str
    raw_complaint: str
    complaint_category: str
    facts: Dict[str, Any]
    followups: List[Dict[str, Any]]
    answers: List[Dict[str, Any]]
    retrieved_rules: List[Dict[str, Any]]
    matched_rule_id: str
    urgency: str
    department: str
    reasoning: str
    unknown_fields: List[str]
    escalated: bool
    escalation_reason: Optional[str]
    triage_note_text: str


class HealthResponse(BaseModel):
    status: str = "ok"
