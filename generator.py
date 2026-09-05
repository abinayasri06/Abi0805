import logging
from typing import Dict, Any, List, Optional
from src.models.schemas import TriageResult, TriageNote, ComplaintCategory
from src.llm.gemini_client import get_gemini_client

logger = logging.getLogger("triage.notes.generator")


def generate_triage_note(
    assessment_id: str,
    raw_complaint: str,
    complaint_category: ComplaintCategory,
    initial_facts: Dict[str, Any],
    followup_answers: List[Dict[str, Any]],
    still_unknown: List[str],
    triage_result: TriageResult
) -> TriageNote:
    """
    Produce the final standardized, structured triage note.
    Separates:
    1. Patient originally reported
    2. Follow-up established
    3. Still unknown
    Strictly forbids disease diagnoses and cites the authoritative rule ID.
    """
    # 1. Format "Patient originally reported"
    patient_reported_items = []
    if raw_complaint:
        patient_reported_items.append(f"Description: \"{raw_complaint}\"")
    for k, v in initial_facts.items():
        clean_k = k.replace("_", " ").capitalize()
        patient_reported_items.append(f"{clean_k}: {v}")
    if not patient_reported_items:
        patient_reported_items.append("No specific clinical features initially extracted.")

    # 2. Format "Follow-up established"
    followup_items = []
    for ans in followup_answers:
        field_name = ans.get("field", "").replace("_", " ").capitalize()
        ans_val = ans.get("answer", "")
        followup_items.append(f"{field_name}: {ans_val}")
    if not followup_items:
        followup_items.append("None (intake completed without additional follow-up answers)")

    # 3. Format "Still unknown"
    unknown_items = []
    for u in still_unknown:
        unknown_items.append(u.replace("_", " ").capitalize())
    if not unknown_items:
        unknown_items.append("None (all required pathway criteria established)")

    # 4. Safety status description
    if triage_result.escalated:
        safety_status = f"HUMAN REVIEW REQUIRED - {triage_result.escalation_reason or 'Clinical safety review required'}"
    else:
        safety_status = "Standard protocol recommendation (Human review not required)"

    # 5. Build reason/explanation
    why_recommendation = triage_result.reasoning
    # Attempt Gemini explanation of the already-determined rule if available
    client = get_gemini_client()
    if client.is_available and not triage_result.escalated:
        try:
            prompt = f"""Summarize why this triage recommendation was made based ONLY on the following rule match.
Do NOT change the urgency level ({triage_result.urgency}) or the department ({triage_result.department}).
Do NOT diagnose any medical condition or disease.

Rule ID: {triage_result.rule_id}
Matched conditions: {triage_result.matched_conditions}
Clinical evidence: {triage_result.evidence_text}

Provide a concise, 2-sentence clinical explanation of why this routing was selected."""
            ai_exp = client.generate_text(prompt)
            if ai_exp:
                why_recommendation = f"{ai_exp} (Matched conditions: {triage_result.matched_conditions})"
        except Exception as e:
            logger.info("Gemini explanation generation skipped: %s", e)

    # 6. Build the formatted ASCII text note
    patient_rep_lines = "\n".join(f"- {item}" for item in patient_reported_items)
    followup_lines = "\n".join(f"- {item}" for item in followup_items)
    unknown_lines = "\n".join(f"- {item}" for item in unknown_items)

    formatted_text = f"""-----------------------------------------
TRIAGE NOTE
-----------------------------------------

Complaint:
{complaint_category.value.replace('_', ' ').capitalize()}

Recommended urgency:
{triage_result.urgency}

Recommended department:
{triage_result.department}

Rule:
{triage_result.rule_id}

Why this recommendation:
{why_recommendation}

Evidence / Clinical Protocol:
{triage_result.evidence_text}

Patient originally reported:
{patient_rep_lines}

Follow-up established:
{followup_lines}

Still unknown:
{unknown_lines}

Safety status:
{safety_status}

Diagnosis:
Not provided (decision support only)
-----------------------------------------"""

    return TriageNote(
        assessment_id=assessment_id,
        complaint=complaint_category.value.replace('_', ' ').capitalize(),
        recommended_urgency=triage_result.urgency,
        recommended_department=triage_result.department,
        rule_id=triage_result.rule_id,
        why_recommendation=why_recommendation,
        patient_reported=patient_reported_items,
        followup_established=followup_items,
        still_unknown=unknown_items,
        safety_status=safety_status,
        diagnosis="Not provided (decision support only)",
        formatted_text=formatted_text
    )
