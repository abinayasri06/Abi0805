import json
import os
import logging
from typing import List, Dict, Any, Optional
from src.config import FOLLOWUP_PATH
from src.models.schemas import ComplaintCategory, FollowUpQuestion

logger = logging.getLogger("triage.llm.followup")


def load_question_library() -> Dict[str, Dict[str, Any]]:
    """Load pre-configured clinical follow-up questions."""
    if os.path.exists(FOLLOWUP_PATH):
        with open(FOLLOWUP_PATH, "r", encoding="utf-8") as f:
            return json.load(f)
    return {}


def generate_followup_questions(
    complaint_category: ComplaintCategory,
    missing_fields: List[str],
    max_questions: int = 3
) -> List[FollowUpQuestion]:
    """
    Generate targeted follow-up questions for missing clinical fields.
    Restricts questions only to unknown fields necessary for deterministic rule evaluation.
    """
    if complaint_category == ComplaintCategory.UNCLEAR or not missing_fields:
        return []

    library = load_question_library()
    category_key = complaint_category.value
    cat_library = library.get(category_key, {})

    questions: List[FollowUpQuestion] = []
    
    for field in missing_fields:
        if len(questions) >= max_questions:
            break
            
        field_info = cat_library.get(field)
        if field_info:
            questions.append(FollowUpQuestion(
                id=f"q_{field}",
                field=field,
                question=field_info.get("question", f"Could you provide more details regarding {field}?"),
                options=field_info.get("options", ["Yes", "No", "I don't know"]),
                type=field_info.get("type", "text")
            ))
        else:
            # Safe generic clinical prompt for rule field
            human_label = field.replace("_", " ")
            questions.append(FollowUpQuestion(
                id=f"q_{field}",
                field=field,
                question=f"Regarding your {complaint_category.value.lower().replace('_', ' ')}, can you tell us about: {human_label}?",
                options=["Yes", "No", "I don't know"],
                type="text"
            ))

    return questions
