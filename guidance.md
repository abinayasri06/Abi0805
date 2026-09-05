# Clinical Intake and Decision Support Guidance

## 1. Principles of Intake Triage
The role of this assistant is **urgency routing and safety triage decision-support**.
Under no circumstances does this system provide a medical diagnosis, predict etiology, or replace qualified medical evaluation.

### Core Safeguards
- **Non-Diagnostic Rule**: Symptoms never equal a disease. A symptom report triggers a routing recommendation, not a diagnosis.
- **Rule Citation**: Every triage recommendation must link directly to an approved protocol rule ID.
- **Authoritative Determinism**: Gemini/LLMs interpret patient natural language and detect missing information, but the final routing determination is made strictly by the deterministic Python rule engine.
- **Explicit Human Escalation**: Whenever information is missing, conflicting, ambiguous, or flags a high-risk red condition, the system assigns `HUMAN REVIEW REQUIRED`.
- **Unknown is Not Negative**: Patient answers stating "I don't know" or omitted variables must be preserved as `"unknown"`, never converted to negative, false, or zero.

---

## 2. Chief Complaint Pathways

### 2.1 Chest Pain (CP)
- **Emergency Indicators (CP-001)**: Active acute chest discomfort with dyspnea, radiating distress, or signs of hypoperfusion. Requires immediate Emergency Department resuscitation level triage.
- **Urgent Indicators (CP-002)**: Active chest discomfort without dyspnea, stable presentation requiring rapid ECG and troponin screening within 60 minutes.
- **Non-Urgent Indicators (CP-003)**: Fully resolved atypical discomfort without cardiopulmonary features.

### 2.2 Breathing Difficulty (BD)
- **Emergency Indicators (BD-001)**: Acute respiratory distress, inability to complete full sentences in one breath, stridor, or cyanosis.
- **Urgent Indicators (BD-002)**: Moderate shortness of breath with preserved speech fluency, audible wheeze, or subacute cough.
- **Non-Urgent Indicators (BD-003)**: Resolved episodic dyspnea or mild cold-associated nasal congestion.

### 2.3 Fever (FEV)
- **Emergency Indicators (FEV-001)**: Febrile illness co-occurring with central nervous system signs (nuchal rigidity, delirium, altered sensorium) or non-blanching purpuric rash.
- **Urgent Indicators (FEV-002)**: Persistent pyrexia lasting beyond 72 hours, or vulnerable demographics requiring diagnostic workup.
- **Non-Urgent Indicators (FEV-003)**: Acute uncomplicated fever under 3 days with benign systemic symptoms.

### 2.4 Injury / Trauma (INJ)
- **Emergency Indicators (INJ-001)**: Obvious anatomical deformity, open fracture, pulsatile hemorrhage, or distal neurovascular compromise.
- **Urgent Indicators (INJ-002)**: Inability to bear weight on injured lower extremity, joint effusion, or high-force focal impact requiring X-ray imaging.
- **Non-Urgent Indicators (INJ-003)**: Minor closed contusion or abrasion with intact range of motion and weight-bearing capability.

### 2.5 Abdominal Pain (AP)
- **Emergency Indicators (AP-001)**: Board-like muscular rigidity, hematemesis, melena, hemodynamically unstable syncope, or excruciating acute pain.
- **Urgent Indicators (AP-002)**: Progressively worsening abdominal discomfort, localized peritonism (e.g. right lower quadrant), or persistent vomiting.
- **Non-Urgent Indicators (AP-003)**: Mild generalized transient discomfort with no progression, fever, or peritoneal signs.

---

## 3. Human Review & Escalation Criteria
Human clinical review must be triggered if:
1. Patient's complaint does not match one of the 5 primary pathways.
2. Crucial discriminator fields remain `"unknown"` after follow-up.
3. Natural language processing encounters contradictory or garbled input.
4. Gemini API experiences network failure, timeout, or schema validation error.
5. High-risk red-flag conditions are met.
