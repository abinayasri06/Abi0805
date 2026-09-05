import os
import json
import logging
import numpy as np
from typing import List, Dict, Any, Optional
from src.config import EMBEDDINGS_PATH, METADATA_PATH, RULES_PATH
from src.retrieval.embeddings import get_text_embedding

logger = logging.getLogger("triage.retrieval.retriever")


class RuleRetriever:
    """Local retrieval engine using NumPy cosine similarity and precomputed embeddings."""

    def __init__(self):
        self.embeddings: Optional[np.ndarray] = None
        self.metadata: List[Dict[str, Any]] = []
        self.rules_by_id: Dict[str, Dict[str, Any]] = {}
        self.load_index()

    def load_index(self) -> None:
        """Load precomputed embeddings and metadata from disk."""
        try:
            if os.path.exists(EMBEDDINGS_PATH) and os.path.exists(METADATA_PATH):
                self.embeddings = np.load(str(EMBEDDINGS_PATH))
                with open(METADATA_PATH, "r", encoding="utf-8") as f:
                    self.metadata = json.load(f)
                logger.info("Loaded %d precomputed embeddings from %s", len(self.metadata), EMBEDDINGS_PATH)
            else:
                logger.warning("Precomputed embeddings not found at %s. Initializing empty index.", EMBEDDINGS_PATH)
                self.embeddings = np.zeros((0, 768), dtype=np.float32)
                self.metadata = []

            # Also load raw rules for full rule data lookup
            if os.path.exists(RULES_PATH):
                with open(RULES_PATH, "r", encoding="utf-8") as f:
                    rules_data = json.load(f)
                    self.rules_by_id = {r["rule_id"]: r for r in rules_data}
            else:
                self.rules_by_id = {}
        except Exception as e:
            logger.error("Failed to load local retrieval index: %s", e)
            self.embeddings = np.zeros((0, 768), dtype=np.float32)
            self.metadata = []

    def retrieve(self, query: str, complaint_category: Optional[str] = None, top_k: int = 3) -> List[Dict[str, Any]]:
        """
        Retrieve the top-k most relevant rules using cosine similarity.
        Prioritizes rules matching the complaint category if specified.
        """
        if not query or self.embeddings is None or len(self.metadata) == 0:
            return []

        query_vec = get_text_embedding(query, dim=self.embeddings.shape[1] if self.embeddings.ndim > 1 else 768)

        # Compute cosine similarity
        norm_query = np.linalg.norm(query_vec)
        if norm_query < 1e-6:
            return []

        # Vectorized cosine similarity
        norms = np.linalg.norm(self.embeddings, axis=1)
        norms[norms == 0] = 1e-6
        similarities = np.dot(self.embeddings, query_vec) / (norms * norm_query)

        # Apply boost for complaint category match
        adjusted_scores = np.copy(similarities)
        if complaint_category:
            norm_cat = complaint_category.upper()
            for idx, meta in enumerate(self.metadata):
                if meta.get("complaint", "").upper() == norm_cat:
                    adjusted_scores[idx] += 0.35  # Category relevance boost

        top_indices = np.argsort(adjusted_scores)[::-1][:top_k]

        results = []
        for idx in top_indices:
            score = float(similarities[idx])
            meta = self.metadata[idx]
            rule_id = meta.get("rule_id")
            full_rule = self.rules_by_id.get(rule_id, {})
            results.append({
                "rule_id": rule_id,
                "complaint": meta.get("complaint"),
                "urgency": meta.get("urgency"),
                "department": meta.get("department"),
                "evidence_text": meta.get("evidence_text") or full_rule.get("evidence_text", ""),
                "action": full_rule.get("action", ""),
                "reasoning": full_rule.get("reasoning", ""),
                "conditions": full_rule.get("conditions", {}),
                "required_information": full_rule.get("required_information", []),
                "escalation_condition": full_rule.get("escalation_condition", ""),
                "similarity_score": round(score, 4)
            })

        return results


_retriever_instance: Optional[RuleRetriever] = None


def get_retriever() -> RuleRetriever:
    """Singleton getter for RuleRetriever."""
    global _retriever_instance
    if _retriever_instance is None:
        _retriever_instance = RuleRetriever()
    return _retriever_instance
