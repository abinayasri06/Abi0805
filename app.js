// State Management
const state = {
  currentAssessmentId: null,
  rawComplaint: "",
  complaintCategory: "",
  initialFacts: {},
  unknownFields: [],
  followupQuestions: [],
  answers: {},
  triageResult: null,
  triageNote: null,
};

// DOM Elements
const patientInputText = document.getElementById("patient-input-text");
const charCurrent = document.getElementById("char-current");
const btnClear = document.getElementById("btn-clear");
const btnStartAssessment = document.getElementById("btn-start-assessment");
const demoButtonsContainer = document.getElementById("demo-buttons-container");

const intakeSection = document.getElementById("intake-section");
const followupSection = document.getElementById("followup-section");
const resultSection = document.getElementById("result-section");
const loadingPanel = document.getElementById("loading-panel");
const loadingStatusText = document.getElementById("loading-status-text");

const categoryBadge = document.getElementById("category-badge");
const initialFactsChips = document.getElementById("initial-facts-chips");
const questionsContainer = document.getElementById("questions-container");
const btnSubmitAnswers = document.getElementById("btn-submit-answers");
const btnBackToIntake = document.getElementById("btn-back-to-intake");

const urgencyHeaderBar = document.getElementById("urgency-header-bar");
const resUrgencyLevel = document.getElementById("res-urgency-level");
const resDepartment = document.getElementById("res-department");
const resRuleId = document.getElementById("res-rule-id");
const resEscalationBanner = document.getElementById("res-escalation-banner");
const resEscalationReason = document.getElementById("res-escalation-reason");
const resReasoning = document.getElementById("res-reasoning");
const resPatientReported = document.getElementById("res-patient-reported");
const resFollowupEstablished = document.getElementById("res-followup-established");
const resRemainingUnknown = document.getElementById("res-remaining-unknown");
const resEvidence = document.getElementById("res-evidence");
const resFormattedNote = document.getElementById("res-formatted-note");
const btnCopyNote = document.getElementById("btn-copy-note");
const btnNewAssessment = document.getElementById("btn-new-assessment");

// Initialize application
document.addEventListener("DOMContentLoaded", () => {
  loadDemoCases();
  setupEventListeners();
});

// Event Listeners
function setupEventListeners() {
  patientInputText.addEventListener("input", () => {
    charCurrent.textContent = patientInputText.value.length;
  });

  btnClear.addEventListener("click", () => {
    patientInputText.value = "";
    charCurrent.textContent = "0";
  });

  btnStartAssessment.addEventListener("click", handleStartAssessment);
  btnSubmitAnswers.addEventListener("click", handleSubmitFollowups);
  btnBackToIntake.addEventListener("click", () => navigateStep(1));
  btnNewAssessment.addEventListener("click", resetToNewAssessment);

  btnCopyNote.addEventListener("click", () => {
    if (state.triageNote && state.triageNote.formatted_text) {
      navigator.clipboard.writeText(state.triageNote.formatted_text);
      const originalText = btnCopyNote.innerHTML;
      btnCopyNote.innerHTML = "✓ Copied!";
      setTimeout(() => {
        btnCopyNote.innerHTML = originalText;
      }, 2000);
    }
  });
}

// Stepper Navigation
function navigateStep(step) {
  intakeSection.classList.add("hidden");
  followupSection.classList.add("hidden");
  resultSection.classList.add("hidden");
  loadingPanel.classList.add("hidden");

  // Reset steps classes
  for (let i = 1; i <= 4; i++) {
    const el = document.getElementById(`step-${i}-indicator`);
    const line = document.getElementById(`line-${i - 1}`);
    if (el) {
      el.classList.remove("active", "completed");
      if (i < step) el.classList.add("completed");
      else if (i === step) el.classList.add("active");
    }
    if (line) {
      line.classList.remove("completed");
      if (i <= step) line.classList.add("completed");
    }
  }

  if (step === 1) intakeSection.classList.remove("hidden");
  else if (step === 2) followupSection.classList.remove("hidden");
  else if (step === 4) resultSection.classList.remove("hidden");
}

function showLoading(msg = "Analyzing clinical description...") {
  loadingStatusText.textContent = msg;
  intakeSection.classList.add("hidden");
  followupSection.classList.add("hidden");
  resultSection.classList.add("hidden");
  loadingPanel.classList.remove("hidden");
}

function hideLoading() {
  loadingPanel.classList.add("hidden");
}

// Load Demo Cases from API
async function loadDemoCases() {
  try {
    const res = await fetch("/api/demo-cases");
    if (!res.ok) return;
    const cases = await res.json();
    demoButtonsContainer.innerHTML = "";

    cases.forEach((c) => {
      const btn = document.createElement("button");
      btn.type = "button";
      btn.className = "demo-btn";
      btn.innerHTML = `
        <span class="demo-btn-title">${c.title}</span>
        <span class="demo-btn-cat">${c.category} &bull; Click to load</span>
      `;
      btn.addEventListener("click", () => {
        patientInputText.value = c.description;
        charCurrent.textContent = c.description.length;
        patientInputText.scrollIntoView({ behavior: "smooth" });
      });
      demoButtonsContainer.appendChild(btn);
    });
  } catch (err) {
    console.warn("Could not load demo presets:", err);
  }
}

// Step 1: Start Intake Assessment
async function handleStartAssessment() {
  const text = patientInputText.value.trim();
  if (!text) {
    alert("Please enter a description of your symptoms before starting.");
    patientInputText.focus();
    return;
  }

  showLoading("Analyzing clinical description and extracting structured facts...");

  try {
    const res = await fetch("/api/intake", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ text }),
    });

    const data = await res.json();
    if (!res.ok) {
      throw new Error(data.detail || "Intake processing failed.");
    }

    state.currentAssessmentId = data.assessment_id;
    state.rawComplaint = text;
    state.complaintCategory = data.complaint_category;
    state.initialFacts = data.facts || {};
    state.unknownFields = data.unknown || [];
    state.followupQuestions = data.followup_questions || [];
    state.answers = {};

    hideLoading();

    // If ambiguous / unclear or no follow-up questions required, assess immediately
    if (data.complaint_category === "UNCLEAR" || !data.needs_followup) {
      await executeAssessment();
    } else {
      renderFollowupScreen();
      navigateStep(2);
    }
  } catch (err) {
    hideLoading();
    navigateStep(1);
    alert("Intake Error: " + err.message);
  }
}

// Step 2: Render Follow-up Questions Screen
function renderFollowupScreen() {
  categoryBadge.textContent = `PATHWAY: ${state.complaintCategory.replace("_", " ")}`;

  // Render initial facts chips
  initialFactsChips.innerHTML = "";
  const factKeys = Object.keys(state.initialFacts);
  if (factKeys.length === 0) {
    initialFactsChips.innerHTML = `<span class="chip">No specific facts pre-filled</span>`;
  } else {
    factKeys.forEach((k) => {
      const chip = document.createElement("span");
      chip.className = "chip";
      chip.innerHTML = `<strong>${k.replace("_", " ")}:</strong> ${state.initialFacts[k]}`;
      initialFactsChips.appendChild(chip);
    });
  }

  // Render Questions
  questionsContainer.innerHTML = "";
  state.followupQuestions.forEach((q, idx) => {
    const qCard = document.createElement("div");
    qCard.className = "question-item";

    const qTitle = document.createElement("div");
    qTitle.className = "question-text";
    qTitle.textContent = `${idx + 1}. ${q.question}`;
    qCard.appendChild(qTitle);

    const optGroup = document.createElement("div");
    optGroup.className = "options-group";

    q.options.forEach((opt) => {
      const optBtn = document.createElement("button");
      optBtn.type = "button";
      optBtn.className = "option-btn";
      if (opt.toLowerCase().includes("don't know")) {
        optBtn.classList.add("btn-unknown");
      }
      optBtn.textContent = opt;

      optBtn.addEventListener("click", () => {
        // Toggle active selection
        optGroup.querySelectorAll(".option-btn").forEach((b) => b.classList.remove("selected"));
        optBtn.classList.add("selected");
        state.answers[q.field] = opt;
      });

      optGroup.appendChild(optBtn);
    });

    qCard.appendChild(optGroup);
    questionsContainer.appendChild(qCard);
  });
}

// Step 3: Submit Answers & Assess
async function handleSubmitFollowups() {
  showLoading("Submitting follow-up information & evaluating deterministic rules...");

  // Package answers
  const answersList = [];
  state.followupQuestions.forEach((q) => {
    const chosenAnswer = state.answers[q.field] || "I don't know";
    answersList.push({
      field: q.field,
      answer: chosenAnswer,
    });
  });

  try {
    const followRes = await fetch("/api/followup", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        assessment_id: state.currentAssessmentId,
        answers: answersList,
      }),
    });

    if (!followRes.ok) {
      const err = await followRes.json();
      throw new Error(err.detail || "Failed to update answers.");
    }

    await executeAssessment();
  } catch (err) {
    hideLoading();
    navigateStep(2);
    alert("Evaluation Error: " + err.message);
  }
}

// Execute Final Deterministic Assessment
async function executeAssessment() {
  showLoading("Executing authoritative Python rule engine & citing protocol evidence...");

  try {
    const assessRes = await fetch("/api/assess", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ assessment_id: state.currentAssessmentId }),
    });

    const data = await assessRes.json();
    if (!assessRes.ok) {
      throw new Error(data.detail || "Final assessment failed.");
    }

    state.triageResult = data.triage_result;
    state.triageNote = data.triage_note;

    hideLoading();
    renderResultScreen();
    navigateStep(4);
  } catch (err) {
    hideLoading();
    alert("Assessment Error: " + err.message);
  }
}

// Step 4: Render Triage Result & Note
function renderResultScreen() {
  const tr = state.triageResult;
  const tn = state.triageNote;

  resUrgencyLevel.textContent = tr.urgency;
  resDepartment.textContent = tr.department;
  resRuleId.textContent = tr.rule_id;

  // Header urgency style
  urgencyHeaderBar.className = "urgency-header";
  if (tr.urgency === "EMERGENCY") urgencyHeaderBar.classList.add("urgency-emergency");
  else if (tr.urgency === "URGENT") urgencyHeaderBar.classList.add("urgency-urgent");
  else if (tr.urgency === "NON_URGENT") urgencyHeaderBar.classList.add("urgency-routine");
  else urgencyHeaderBar.classList.add("urgency-human-review");

  // Escalation alert
  if (tr.escalated) {
    resEscalationBanner.classList.remove("hidden");
    resEscalationReason.textContent = tr.escalation_reason || "Clinical safety protocol mandates direct human assessment.";
  } else {
    resEscalationBanner.classList.add("hidden");
  }

  // Why Recommendation
  resReasoning.textContent = tr.reasoning;

  // Patient Reported
  resPatientReported.innerHTML = "";
  (tn.patient_reported || []).forEach((item) => {
    const li = document.createElement("li");
    li.textContent = item;
    resPatientReported.appendChild(li);
  });

  // Follow-up Established
  resFollowupEstablished.innerHTML = "";
  (tn.followup_established || []).forEach((item) => {
    const li = document.createElement("li");
    li.textContent = item;
    resFollowupEstablished.appendChild(li);
  });

  // Remaining Unknown
  resRemainingUnknown.innerHTML = "";
  (tn.still_unknown || []).forEach((item) => {
    const li = document.createElement("li");
    li.textContent = item;
    resRemainingUnknown.appendChild(li);
  });

  // Evidence
  resEvidence.textContent = tr.evidence_text || "Clinical protocol verified against local guideline library.";

  // Formatted Clinical Note
  resFormattedNote.textContent = tn.formatted_text;

  // Scroll into view
  resultSection.scrollIntoView({ behavior: "smooth" });
}

// Reset for a fresh intake
function resetToNewAssessment() {
  state.currentAssessmentId = null;
  state.rawComplaint = "";
  state.complaintCategory = "";
  state.initialFacts = {};
  state.unknownFields = [];
  state.followupQuestions = [];
  state.answers = {};
  state.triageResult = null;
  state.triageNote = null;

  patientInputText.value = "";
  charCurrent.textContent = "0";

  navigateStep(1);
  window.scrollTo({ top: 0, behavior: "smooth" });
}
