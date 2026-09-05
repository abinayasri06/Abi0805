import os
import logging
from typing import Optional, Dict, Any
from src.config import GEMINI_API_KEY, GEMINI_MODEL

logger = logging.getLogger("triage.llm.gemini_client")


class GeminiAPIError(Exception):
    """Raised when Gemini API encounters an error or is unavailable."""
    pass


class GeminiClient:
    """Wrapper for Google GenAI SDK with graceful error handling and retry support."""

    def __init__(self, api_key: Optional[str] = None):
        self.api_key = api_key or os.getenv("GEMINI_API_KEY") or GEMINI_API_KEY
        self._client = None
        if self.api_key:
            try:
                from google import genai
                self._client = genai.Client(api_key=self.api_key)
                logger.info("Gemini client initialized successfully.")
            except Exception as e:
                logger.warning("Failed to initialize Google GenAI client: %s", e)
                self._client = None
        else:
            logger.info("No GEMINI_API_KEY detected in environment.")

    @property
    def is_available(self) -> bool:
        """Check if live Gemini client is configured and ready."""
        return self._client is not None

    def generate_json(self, prompt: str, system_instruction: Optional[str] = None, model: Optional[str] = None) -> str:
        """
        Generate JSON content with Gemini with 1 retry.
        Raises GeminiAPIError if unavailable or failed.
        """
        if not self._client:
            raise GeminiAPIError("GEMINI_API_KEY is not configured or client failed to initialize.")

        target_model = model or GEMINI_MODEL

        for attempt in range(2):
            try:
                from google.genai import types
                config = types.GenerateContentConfig(
                    response_mime_type="application/json",
                    temperature=0.1,
                    system_instruction=system_instruction or "You are a clinical intake assistant. Extract structured facts. Do NOT diagnose."
                )
                response = self._client.models.generate_content(
                    model=target_model,
                    contents=prompt,
                    config=config
                )
                if response and response.text:
                    return response.text.strip()
                raise GeminiAPIError("Empty response received from Gemini.")
            except Exception as e:
                logger.warning("Gemini attempt %d failed: %s", attempt + 1, e)
                if attempt == 1:
                    raise GeminiAPIError(f"Gemini generation failed after retry: {e}")

        raise GeminiAPIError("Gemini generation failed.")

    def generate_text(self, prompt: str, system_instruction: Optional[str] = None, model: Optional[str] = None) -> str:
        """Generate human-readable text explanation with Gemini."""
        if not self._client:
            raise GeminiAPIError("GEMINI_API_KEY is not configured.")

        target_model = model or GEMINI_MODEL
        try:
            from google.genai import types
            config = types.GenerateContentConfig(
                temperature=0.2,
                system_instruction=system_instruction or "You are a clinical intake assistant summarizing rule-based triage results. Do NOT provide medical diagnoses."
            )
            response = self._client.models.generate_content(
                model=target_model,
                contents=prompt,
                config=config
            )
            if response and response.text:
                return response.text.strip()
            raise GeminiAPIError("Empty response received from Gemini text generation.")
        except Exception as e:
            logger.warning("Gemini text generation failed: %s", e)
            raise GeminiAPIError(f"Gemini text generation failed: {e}")


_client_instance: Optional[GeminiClient] = None


def get_gemini_client() -> GeminiClient:
    """Singleton getter for GeminiClient."""
    global _client_instance
    if _client_instance is None:
        _client_instance = GeminiClient()
    return _client_instance
