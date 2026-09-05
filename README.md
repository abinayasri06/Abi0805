# AI-Based Personalized Learning Path Recommendation System 🎓

An intelligent, adaptive e-learning recommendation system that diagnoses a learner's baseline skill competency, analyzes career specializations and learning preferences, and dynamically constructs an individualized curriculum using **Knowledge Graph Directed Acyclic Graphs (DAGs)**, **Content-Based Vector Filtering (TF-IDF + Cosine Similarity)**, and a **Closed-Loop Adaptive Feedback Engine**.

---

## 🌟 Key Features

1. **Intelligent Learner Profiling:**
   - Multi-dimensional profiling: Target specialization, self-assessed competency, study commitment (hours/week), preferred media style (Videos, Interactive Coding, Documentation, Guided Projects), and keyword interests.
   - 5 Supported Career Tracks:
     - **Artificial Intelligence & Machine Learning**
     - **Full-Stack Web Development**
     - **Data Science & Analytics**
     - **Cybersecurity Essentials**
     - **Cloud Computing & DevOps**

2. **Diagnostic Skill Assessment & Gap Analysis:**
   - Evaluates foundational, intermediate, and advanced questions tailored to the chosen career track.
   - Identifies prerequisite blindspots and automatically fast-tracks concepts the learner has already mastered.

3. **Knowledge Graph DAG Engine (NetworkX / Pure Python):**
   - Implements **Kahn's Topological Sorting Algorithm** to ensure mathematical ordering of topics ($G = (V, E)$), preventing circular dependencies and guaranteeing prerequisites precede advanced modules.

4. **Hybrid AI Content Recommendation:**
   - Uses **TF-IDF Vector Space Modeling** and **Cosine Similarity** to match learner goals and preferences against a curated catalog of industry-standard courses (FreeCodeCamp, PyTorch, Fast.ai, Kaggle, PortSwigger, etc.).
   - Employs a multi-factor ranking scoring formula integrating media format matching, difficulty alignment, and rating bonuses.

5. **Adaptive Closed-Loop Feedback:**
   - Real-time milestone mastery checks:
     - Score $< 60\%$: Dynamically marks milestone as *Needs Remediation*, injects targeted reinforcement exercises, and adjusts estimated study hours.
     - Score $\ge 85\%$: Awards an *Honors Mastery* badge and accelerates upcoming schedules.
     - Score $60\% - 84\%$: Marks as successfully *Completed*.

6. **Interactive Visual Dashboard:**
   - Visual progress gauges and interactive Gantt-style syllabus timeline.
   - Competency Radar Chart (Plotly) visualizing categorical mastery (Foundations, Mathematics, Core, Production).
   - Instant export of the personalized curriculum as a downloadable Markdown / Report file.

---

## 🏗️ System Architecture & Workflow

```
+-------------------------------------------------------------+
|                      Learner Portal                         |
|   (Profile: Target Track, Commitment, Learning Format, Exp) |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|               Diagnostic Skill Gap Assessment               |
|       (5-Question Multi-Difficulty Competency Evaluation)   |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                  AI Recommendation Core                     |
|  1. Knowledge Graph (DAG) Prerequisite Topological Sort    |
|  2. Content-Based TF-IDF & Cosine Similarity Matching       |
|  3. Multi-Criteria Ranking (Media, Difficulty, Rating)      |
+------------------------------+------------------------------+
                               |
                               v
+-------------------------------------------------------------+
|                 Dynamic Milestone Roadmap                   |
|       - Weekly Sprint Schedule (Gantt Timeline)             |
|       - Ranked Course Links & Descriptions                  |
|       - Interactive Milestone Mastery Quiz Verification     |
+------------------------------+------------------------------+
                               |
         +---------------------+---------------------+
         |                                           |
         v (< 60% Score)                             v (>= 85% Score)
+--------------------------------+   +-------------------------------+
|    Remedial Module Injection   |   |   Honors & Accelerated Path   |
| (Khan Academy / Concept Drill) |   |    (Fast-Track Advanced)      |
+--------------------------------+   +-------------------------------+
```

---

## 🧮 Mathematical & Algorithmic Formulation

### 1. Directed Acyclic Graph (DAG) Prerequisite Ordering
Let the curriculum be represented as a directed graph $G = (V, E)$, where:
- $V = \{v_1, v_2, \dots, v_n\}$ represents curriculum topics.
- Directed edge $(u, v) \in E$ denotes that topic $u$ is an essential pedagogical prerequisite for topic $v$.

Using **Kahn's Algorithm**, topics are resolved into a linear topological order $L$:
$$\text{in-degree}(v) = |\{u \in V \mid (u, v) \in E\}|$$
Topics with $\text{in-degree} = 0$ are scheduled first; upon scheduling, outgoing edges are removed and next zero in-degree topics are enqueued.

### 2. Content-Based Cosine Similarity
Learner query vector $\vec{u}$ and course metadata vector $\vec{c}_i$ are vectorized using Term Frequency-Inverse Document Frequency (TF-IDF):
$$\text{TF-IDF}(t, d, D) = \text{TF}(t, d) \times \log\left(\frac{|D|}{1 + |\{d' \in D \mid t \in d'\}|}\right)$$

The semantic alignment is given by Cosine Similarity:
$$\text{Sim}(\vec{u}, \vec{c}_i) = \frac{\vec{u} \cdot \vec{c}_i}{\|\vec{u}\|_2 \|\vec{c}_i\|_2}$$

The final recommendation score $S(c_i)$ combines semantic similarity with preference bonuses:
$$S(c_i) = \text{Sim}(\vec{u}, \vec{c}_i) + \beta_{\text{media}} \cdot \mathbb{I}(\text{format matched}) + \beta_{\text{diff}} \cdot \mathbb{I}(\text{level matched}) + \beta_{\text{rating}} \cdot (\text{rating} - 4.0)$$

---

## 📂 Project Structure

```
Mini project/
│
├── app.py                     # Streamlit Interactive Dashboard UI
├── requirements.txt           # Python Package Dependencies
├── README.md                  # Complete Academic & Technical Documentation
│
├── core/
│   ├── __init__.py
│   ├── knowledge_graph.py     # DAG Traversal & Topological Sorting Engine
│   ├── recommender.py         # Content-Based TF-IDF & Cosine Similarity Engine
│   └── adaptive_engine.py     # Diagnostic & Real-Time Dynamic Feedback Engine
│
├── data/
│   ├── skill_graph.json       # Prerequisite ontology & topic metadata across 5 domains
│   ├── courses_dataset.json   # Curated courses repository with metadata & resource URLs
│   ├── diagnostic_quizzes.json# Domain-specific baseline assessment questionnaires
│   └── saved_profiles.json    # Persistent user states & progress storage
│
├── utils/
│   ├── __init__.py
│   ├── visualization.py       # Plotly radar charts, progress gauges & Gantt charts
│   └── storage.py             # User progress saving & retrieval logic
│
└── tests/
    ├── __init__.py
    └── test_recommender.py    # Unit test suite for verification
```

---

## 🚀 Installation & Execution Guide

### Prerequisites
- Python 3.8, 3.9, 3.10, 3.11, or 3.12 installed on your system.

### Step 1: Clone or Open Project Directory
```powershell
cd "c:\Users\ABINAYASRI M\OneDrive\Documents\Mini project"
```

### Step 2: Install Dependencies
```powershell
pip install -r requirements.txt
```

### Step 3: Run Unit Tests
Verify core algorithm correctness:
```powershell
python -m unittest tests/test_recommender.py
```

### Step 4: Launch the Interactive Web Application
```powershell
streamlit run app.py
```
The application will automatically launch in your default web browser at `http://localhost:8501`.

---

## 🎓 Viva Voce & Project Review Defense Q&A

### Q1: Why use a Directed Acyclic Graph (DAG) instead of traditional linear playlists?
**Answer:** Traditional playlists present courses linearly regardless of logical prerequisites. In technical domains (like AI or Full-Stack), attempting to learn deep learning before linear algebra or React before JavaScript leads to student dropout. A DAG mathematically guarantees that all prerequisite ancestors $\{u \in \text{Ancestors}(v)\}$ must be completed before node $v$ is unlocked, eliminating circular dependencies.

### Q2: How does the system handle "Cold Start" for new students?
**Answer:** Cold start is addressed through our **2-stage onboarding mechanism**:
1. Explicit preference elicitation (target specialization, hours/week, preferred media format).
2. Diagnostic baseline testing that computes an immediate skill gap score and initializes the prerequisite tree.

### Q3: How does the recommendation system adapt when a student fails a milestone?
**Answer:** When a student scores below $60\%$ on a milestone assessment, the `AdaptiveEngine`:
1. Transitions the topic state to `Needs Remediation`.
2. Automatically inserts curated remedial resources (targeted conceptual drills / Khan Academy modules).
3. Adds buffer hours to the sprint to ensure mastery before advancing to downstream dependent nodes.

### Q4: What are the advantages of Content-Based Filtering here over Collaborative Filtering?
**Answer:** Collaborative filtering requires a massive matrix of thousands of active users and interactions to find similar peers (suffering severely from sparse user data in smaller platforms). Content-based filtering operates effectively on item metadata (topics, difficulty, media type, tags) and user profiles from day one with zero cold-start latency.
