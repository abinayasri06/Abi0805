import os
import hashlib
import logging
import numpy as np
from typing import Optional
from src.config import GEMINI_API_KEY, GEMINI_EMBEDDING_MODEL

logger = logging.getLogger("triage.retrieval.embeddings")


def generate_local_semantic_vector(text: str, dim: int = 768) -> np.ndarray:
    """
    Generate a deterministic normalized semantic representation for offline retrieval
    and zero-network local fallback. Uses stable cryptographic digest to ensure cross-process consistency.
    """
    vec = np.zeros(dim, dtype=np.float32)
    clean_text = text.lower().replace("-", " ").replace("_", " ")
    words = clean_text.split()
    for i, word in enumerate(words):
        h = int(hashlib.sha256(word.encode("utf-8")).hexdigest(), 16)
        idx = h % dim
        weight = 1.0 + (1.0 / (i + 1.0))
        vec[idx] += weight
        if i > 0:
            bigram = f"{words[i-1]}_{word}"
            h2 = int(hashlib.sha256(bigram.encode("utf-8")).hexdigest(), 16)
            idx2 = h2 % dim
            vec[idx2] += 1.5 * weight
            
    norm = np.linalg.norm(vec)
    if norm > 1e-6:
        vec = vec / norm
    return vec


def get_text_embedding(text: str, dim: int = 768) -> np.ndarray:
    """
    Convert text to an embedding vector.
    Uses Gemini gemini-embedding-001 if GEMINI_API_KEY is available.
    Falls back to deterministic local semantic vector if API key is absent or network fails.
    """
    api_key = os.getenv("GEMINI_API_KEY") or GEMINI_API_KEY
    if api_key:
        try:
            from google import genai
            client = genai.Client(api_key=api_key)
            resp = client.models.embed_content(
                model=GEMINI_EMBEDDING_MODEL,
                contents=text
            )
            if hasattr(resp, "embeddings") and resp.embeddings:
                raw_values = resp.embeddings[0].values
                vec = np.array(raw_values, dtype=np.float32)
                # Ensure dimension matches target precomputed matrix
                if len(vec) == dim:
                    norm = np.linalg.norm(vec)
                    return vec / norm if norm > 1e-6 else vec
                logger.warning("Gemini embedding returned dim %d, expected %d. Falling back to local vector.", len(vec), dim)
        except Exception as e:
            logger.warning("Gemini embedding API call failed (%s). Falling back to local semantic vector.", e)

    return generate_local_semantic_vector(text, dim=dim)
