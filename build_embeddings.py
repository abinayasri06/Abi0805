import json
import os
import hashlib
import numpy as np

def generate_semantic_vector(text: str, dim: int = 768) -> np.ndarray:
    """Generate a deterministic normalized semantic representation for local RAG retrieval."""
    vec = np.zeros(dim, dtype=np.float32)
    words = text.lower().replace("-", " ").replace("_", " ").split()
    for i, word in enumerate(words):
        # Deterministic hashing into 768-dim space with word and bigram features
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
        vec /= norm
    return vec

def main():
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    data_dir = os.path.join(base_dir, "data")
    rules_path = os.path.join(data_dir, "triage_rules.json")
    
    with open(rules_path, "r", encoding="utf-8") as f:
        rules = json.load(f)
    
    metadata = []
    vectors = []
    
    # Try Gemini embedding if GEMINI_API_KEY is present
    api_key = os.getenv("GEMINI_API_KEY")
    client = None
    if api_key:
        try:
            from google import genai
            client = genai.Client(api_key=api_key)
            print("Using live Gemini API for embedding generation...")
        except Exception as e:
            print(f"Gemini client init note: {e}")
            client = None
            
    for rule in rules:
        content = f"{rule['complaint']} rule {rule['rule_id']}: {rule['reasoning']} Evidence: {rule['evidence_text']} Action: {rule['action']} Escalation: {rule['escalation_condition']}"
        meta = {
            "rule_id": rule["rule_id"],
            "complaint": rule["complaint"],
            "urgency": rule["urgency"],
            "department": rule["department"],
            "text": content,
            "evidence_text": rule["evidence_text"]
        }
        
        vec = None
        if client:
            try:
                # Use gemini-embedding-001 or text-embedding-004
                resp = client.models.embed_content(
                    model="gemini-embedding-001",
                    contents=content
                )
                if hasattr(resp, "embeddings") and resp.embeddings:
                    vec = np.array(resp.embeddings[0].values, dtype=np.float32)
            except Exception as ex:
                print(f"Fallback to semantic vector for {rule['rule_id']}: {ex}")
                vec = None
                
        if vec is None:
            vec = generate_semantic_vector(content, dim=768)
            
        metadata.append(meta)
        vectors.append(vec)
        
    vec_array = np.array(vectors, dtype=np.float32)
    
    npy_path = os.path.join(data_dir, "embeddings.npy")
    json_path = os.path.join(data_dir, "embedding_metadata.json")
    
    np.save(npy_path, vec_array)
    with open(json_path, "w", encoding="utf-8") as f:
        json.dump(metadata, f, indent=2)
        
    print(f"Saved {len(metadata)} embeddings to {npy_path} (shape: {vec_array.shape})")
    print(f"Saved metadata to {json_path}")

if __name__ == "__main__":
    main()
