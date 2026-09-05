import sqlite3
import json
import logging
from datetime import datetime, timezone
from typing import Optional, Dict, Any
from src.config import DB_PATH

logger = logging.getLogger("triage.db")


def get_connection() -> sqlite3.Connection:
    """Create a connection to the SQLite database."""
    DB_PATH.parent.mkdir(parents=True, exist_ok=True)
    conn = sqlite3.connect(str(DB_PATH))
    conn.row_factory = sqlite3.Row
    return conn


get_db = get_connection


def init_db() -> None:
    """Initialize the SQLite schema."""
    with get_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS assessments (
                assessment_id TEXT PRIMARY KEY,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                raw_complaint TEXT NOT NULL,
                complaint_category TEXT NOT NULL,
                facts_json TEXT NOT NULL,
                followups_json TEXT NOT NULL,
                answers_json TEXT NOT NULL,
                retrieved_rules_json TEXT NOT NULL,
                matched_rule_id TEXT,
                urgency TEXT,
                department TEXT,
                reasoning TEXT,
                unknown_fields_json TEXT NOT NULL,
                escalated INTEGER NOT NULL DEFAULT 0,
                escalation_reason TEXT,
                triage_note_text TEXT
            )
        """)
        conn.commit()
    logger.info("Database initialized at %s", DB_PATH)


def save_assessment(
    assessment_id: str,
    raw_complaint: str,
    complaint_category: str,
    facts: Dict[str, Any],
    followups: list,
    unknown_fields: list,
    escalated: bool = False,
    escalation_reason: Optional[str] = None
) -> None:
    """Save an initial intake assessment record."""
    now = datetime.now(timezone.utc).isoformat()
    with get_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("""
            INSERT OR REPLACE INTO assessments (
                assessment_id, created_at, updated_at, raw_complaint, complaint_category,
                facts_json, followups_json, answers_json, retrieved_rules_json,
                matched_rule_id, urgency, department, reasoning,
                unknown_fields_json, escalated, escalation_reason, triage_note_text
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """, (
            assessment_id,
            now,
            now,
            raw_complaint,
            complaint_category,
            json.dumps(facts),
            json.dumps(followups),
            json.dumps([]),
            json.dumps([]),
            None,
            None,
            None,
            None,
            json.dumps(unknown_fields),
            1 if escalated else 0,
            escalation_reason,
            None
        ))
        conn.commit()


def update_assessment_followup(
    assessment_id: str,
    answers: list,
    updated_facts: Dict[str, Any],
    updated_unknown: list
) -> None:
    """Update assessment with follow-up answers and updated facts."""
    now = datetime.now(timezone.utc).isoformat()
    with get_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("""
            UPDATE assessments SET
                answers_json = ?,
                facts_json = ?,
                unknown_fields_json = ?,
                updated_at = ?
            WHERE assessment_id = ?
        """, (
            json.dumps(answers),
            json.dumps(updated_facts),
            json.dumps(updated_unknown),
            now,
            assessment_id
        ))
        conn.commit()


def update_assessment_final(
    assessment_id: str,
    retrieved_rules: list,
    matched_rule_id: str,
    urgency: str,
    department: str,
    reasoning: str,
    escalated: bool,
    escalation_reason: Optional[str],
    triage_note_text: str
) -> None:
    """Finalize assessment record with rule engine results and triage note."""
    now = datetime.now(timezone.utc).isoformat()
    with get_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("""
            UPDATE assessments SET
                retrieved_rules_json = ?,
                matched_rule_id = ?,
                urgency = ?,
                department = ?,
                reasoning = ?,
                escalated = ?,
                escalation_reason = ?,
                triage_note_text = ?,
                updated_at = ?
            WHERE assessment_id = ?
        """, (
            json.dumps(retrieved_rules),
            matched_rule_id,
            urgency,
            department,
            reasoning,
            1 if escalated else 0,
            escalation_reason,
            triage_note_text,
            now,
            assessment_id
        ))
        conn.commit()


def get_assessment(assessment_id: str) -> Optional[Dict[str, Any]]:
    """Retrieve full assessment record by assessment ID."""
    with get_connection() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM assessments WHERE assessment_id = ?", (assessment_id,))
        row = cursor.fetchone()
        if not row:
            return None
        
        return {
            "assessment_id": row["assessment_id"],
            "created_at": row["created_at"],
            "updated_at": row["updated_at"],
            "raw_complaint": row["raw_complaint"],
            "complaint_category": row["complaint_category"],
            "facts": json.loads(row["facts_json"]),
            "followups": json.loads(row["followups_json"]),
            "answers": json.loads(row["answers_json"]),
            "retrieved_rules": json.loads(row["retrieved_rules_json"]),
            "matched_rule_id": row["matched_rule_id"],
            "urgency": row["urgency"],
            "department": row["department"],
            "reasoning": row["reasoning"],
            "unknown_fields": json.loads(row["unknown_fields_json"]),
            "escalated": bool(row["escalated"]),
            "escalation_reason": row["escalation_reason"],
            "triage_note_text": row["triage_note_text"],
        }
