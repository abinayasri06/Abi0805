import json
import logging
import re
from typing import Dict, Any, List, Tuple
from src.models.schemas import ComplaintCategory, StructuredPatientFacts
from src.llm.gemini_client import get_gemini_client, GeminiAPIError

logger = logging.getLogger("triage.llm.extractor")

CATEGORY_REQUIRED_FIELDS: Dict[ComplaintCategory, List[str]] = {
    ComplaintCategory.CHEST_PAIN: [
        "chest_pain_present",
        "breathing_difficulty",
        "current_status",
    ],
    ComplaintCategory.BREATHING_DIFFICULTY: [
        "breathing_difficulty_present",
        "can_speak_full_sentences",
        "current_status",
    ],
    ComplaintCategory.FEVER: [
        "stiff_neck_or_confusion",
        "duration_days",
    ],
    ComplaintCategory.INJURY: [
        "severe_deformity_or_bleeding",
        "can_bear_weight",
    ],
    ComplaintCategory.ABDOMINAL_PAIN: [
        "vomiting_blood_or_rigid_abdomen",
        "is_worsening",
    ],
}

EXTRACTION_SYSTEM_PROMPT = """You are a clinical intake natural language processing engine for TriageAI.
Your task is to analyze everyday patient descriptions and extract structured facts.

CRITICAL CLINICAL RULES:
1. NEVER diagnose the patient. Do not state diseases like "heart attack", "pneumonia", "appendicitis", etc.
2. Categorize the chief complaint into exactly ONE of:
   - "CHEST_PAIN"
   - "BREATHING_DIFFICULTY"
   - "FEVER"
   - "INJURY"
   - "ABDOMINAL_PAIN"
   - "UNCLEAR"
3. If the complaint does not clearly and specifically match one of the 5 categories (or is vague, like "I feel bad", "I am sick", "everything hurts"), choose "UNCLEAR". NEVER guess or force an unclear complaint into a category.
4. If the patient mentions chest pain along with breathing difficulty, classify as "CHEST_PAIN" with facts indicating breathing_difficulty is true.
5. Extract known facts as boolean (true/false) or concise strings.
6. Only record facts the patient actually mentioned.
7. Any required field not mentioned by the patient MUST be listed in the "unknown" array.
8. Output ONLY valid JSON matching this schema:
{
  "complaint_category": "CHEST_PAIN | BREATHING_DIFFICULTY | FEVER | INJURY | ABDOMINAL_PAIN | UNCLEAR",
  "patient_report": "Concise summary of what the patient reported",
  "facts": {
    "key": "value"
  },
  "unknown": ["missing_field_1", "missing_field_2"],
  "confidence_score": 0.0 to 1.0,
  "unclear_reason": null or "reason why unclear"
}
"""


def rule_based_extract(text: str) -> StructuredPatientFacts:
    """
    Deterministic rule-based extractor used as fallback when Gemini API is not configured
    or when Gemini encounters temporary failures.
    """
    clean = text.lower()
    
    # Check for unclear/vague inputs
    vague_phrases = ["feel bad", "sick", "weird", "not well", "pain somewhere", "don't know what is wrong", "something wrong"]
    is_vague = any(p in clean for p in vague_phrases) and len(clean.split()) < 8

    # Category matching
    if any(k in clean for k in ["chest", "heart", "angina", "tight chest", "sternum"]):
        cat = ComplaintCategory.CHEST_PAIN
        facts: Dict[str, Any] = {
            "chest_pain_present": True,
            "breathing_difficulty": any(b in clean for b in ["breath", "short of breath", "trouble breathing", "gasping", "suffocating", "air"]),
            "current_status": "present" if not any(r in clean for r in ["stopped", "gone", "resolved", "better"]) else "resolved",
        }
        if "since this morning" in clean or "this morning" in clean:
            facts["onset"] = "this morning"
        elif "yesterday" in clean:
            facts["onset"] = "yesterday"
        elif "hour" in clean:
            facts["onset"] = "recent hours"

    elif any(k in clean for k in ["breath", "shortness of breath", "trouble breathing", "wheez", "gasp", "suffocating", "can't breathe"]):
        cat = ComplaintCategory.BREATHING_DIFFICULTY
        facts = {
            "breathing_difficulty_present": True,
            "current_status": "present" if not any(r in clean for r in ["stopped", "gone", "resolved"]) else "resolved",
            "can_speak_full_sentences": False if any(s in clean for s in ["can't talk", "struggling to speak", "cannot speak", "hard to talk"]) else True,
        }
        if "sudden" in clean:
            facts["onset"] = "sudden"

    elif any(k in clean for k in ["fever", "temperature", "chills", "hot", "burning up", "sweats"]):
        cat = ComplaintCategory.FEVER
        facts = {
            "stiff_neck_or_confusion": any(s in clean for s in ["stiff neck", "neck", "confused", "confusion", "dizzy", "headache"]),
            "current_symptoms": "feverish feeling",
        }
        if "yesterday" in clean or "day" in clean:
            facts["duration_days"] = "3_days_or_less" if not any(m in clean for m in ["week", "4 days", "5 days", "more than 3"]) else "more_than_3_days"

    elif any(k in clean for k in ["injur", "fall", "fell", "hit", "cut", "sprain", "twisted", "broken", "fracture", "accident", "trauma"]):
        cat = ComplaintCategory.INJURY
        facts = {
            "severe_deformity_or_bleeding": any(d in clean for d in ["bone", "crooked", "deform", "bleeding", "blood gushing", "open wound"]),
            "can_bear_weight": False if any(w in clean for w in ["can't walk", "cannot walk", "can't stand", "cannot bear weight"]) else True,
        }
        if "ankle" in clean:
            facts["location"] = "ankle"
        elif "leg" in clean:
            facts["location"] = "leg"
        elif "arm" in clean or "wrist" in clean:
            facts["location"] = "arm"

    elif any(k in clean for k in ["stomach", "abdomen", "belly", "tummy", "abdominal", "gut", "cramp"]):
        cat = ComplaintCategory.ABDOMINAL_PAIN
        facts = {
            "vomiting_blood_or_rigid_abdomen": any(v in clean for v in ["vomit blood", "vomiting blood", "hard stomach", "board", "black stool"]),
            "is_worsening": any(w in clean for w in ["worse", "worsening", "getting worse", "increasing"]),
        }
        if "since this morning" in clean or "this morning" in clean:
            facts["onset_and_duration"] = "this morning"

    else:
        cat = ComplaintCategory.UNCLEAR
        facts = {}

    if cat == ComplaintCategory.UNCLEAR or is_vague:
        return StructuredPatientFacts(
            complaint_category=ComplaintCategory.UNCLEAR,
            patient_report=text,
            facts={},
            unknown=[],
            confidence_score=0.2,
            unclear_reason="The system could not confidently determine which supported intake pathway applies."
        )

    # Determine missing required fields
    required = CATEGORY_REQUIRED_FIELDS.get(cat, [])
    unknown = [field for field in required if field not in facts]

    return StructuredPatientFacts(
        complaint_category=cat,
        patient_report=text,
        facts=facts,
        unknown=unknown,
        confidence_score=0.9,
        unclear_reason=None
    )


def extract_patient_facts(text: str) -> StructuredPatientFacts:
    """
    Extract structured clinical facts from plain everyday patient language.
    Uses Gemini when available with strict JSON parsing and 1 retry.
    Falls back gracefully to deterministic rule-based extractor on API unavailability.
    """
    client = get_gemini_client()
    if not client.is_available:
        logger.info("Using deterministic fallback extractor (Gemini client offline or not configured).")
        return rule_based_extract(text)

    prompt = f"""Extract structured intake facts from the following patient description.
Patient input:
\"\"\"{text}\"\"\"

Output valid JSON only."""

    raw_json = None
    for attempt in range(2):
        try:
            raw_json = client.generate_json(prompt, system_instruction=EXTRACTION_SYSTEM_PROMPT)
            # Clean possible markdown wrapping if returned
            clean_json = re.sub(r"^```json\s*|\s*```$", "", raw_json.strip())
            data = json.loads(clean_json)

            # Validate complaint category
            cat_str = data.get("complaint_category", "UNCLEAR").upper().strip()
            if cat_str not in ComplaintCategory.__members__:
                cat = ComplaintCategory.UNCLEAR
            else:
                cat = ComplaintCategory(cat_str)

            facts = data.get("facts", {})
            if not isinstance(facts, dict):
                facts = {}

            # Recalculate unknown fields against standard schema
            if cat != ComplaintCategory.UNCLEAR:
                required = CATEGORY_REQUIRED_FIELDS.get(cat, [])
                unknown = [f for f in required if f not in facts or facts[f] in [None, "unknown", "UNKNOWN"]]
            else:
                unknown = []

            return StructuredPatientFacts(
                complaint_category=cat,
                patient_report=data.get("patient_report") or text,
                facts=facts,
                unknown=unknown,
                confidence_score=float(data.get("confidence_score", 0.95 if cat != ComplaintCategory.UNCLEAR else 0.0)),
                unclear_reason=data.get("unclear_reason") if cat == ComplaintCategory.UNCLEAR else None
            )
        except Exception as e:
            logger.warning("Gemini JSON extraction attempt %d error: %s (raw: %s)", attempt + 1, e, raw_json)
            if attempt == 1:
                logger.info("Gemini extraction failed after retry. Escalating to safe deterministic fallback.")
                return rule_based_extract(text)

    return rule_based_extract(text)
