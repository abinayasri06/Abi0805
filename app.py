import os
import json
import streamlit as st
import pandas as pd

from core.knowledge_graph import KnowledgeGraph
from core.recommender import ContentBasedRecommender
from core.adaptive_engine import AdaptiveEngine
from utils.visualization import (
    create_radar_chart,
    create_progress_gauge,
    create_timeline_chart,
    generate_exportable_markdown
)
from utils.storage import save_user_state, load_user_state, list_saved_users

# Page Configuration
st.set_page_config(
    page_title="AI Personalized Learning Path Recommender",
    page_icon="🎓",
    layout="wide",
    initial_sidebar_state="expanded"
)

# Custom Styling
st.markdown("""
<style>
    .main-title {
        font-size: 2.3rem;
        font-weight: 800;
        background: -webkit-linear-gradient(45deg, #4F46E5, #06B6D4);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        margin-bottom: 0.2rem;
    }
    .sub-title {
        font-size: 1.05rem;
        color: #4B5563;
        margin-bottom: 1.2rem;
    }
    .card-box {
        background-color: #F8FAFC;
        border: 1px solid #E2E8F0;
        border-radius: 10px;
        padding: 1.2rem;
        margin-bottom: 1rem;
    }
    .badge-primary {
        background-color: #EEF2FF;
        color: #4F46E5;
        padding: 0.25rem 0.6rem;
        border-radius: 6px;
        font-weight: 600;
        font-size: 0.8rem;
    }
    .badge-success {
        background-color: #ECFDF5;
        color: #059669;
        padding: 0.25rem 0.6rem;
        border-radius: 6px;
        font-weight: 600;
        font-size: 0.8rem;
    }
    .badge-warning {
        background-color: #FEF3C7;
        color: #D97706;
        padding: 0.25rem 0.6rem;
        border-radius: 6px;
        font-weight: 600;
        font-size: 0.8rem;
    }
    .badge-danger {
        background-color: #FEE2E2;
        color: #DC2626;
        padding: 0.25rem 0.6rem;
        border-radius: 6px;
        font-weight: 600;
        font-size: 0.8rem;
    }
    .stButton>button {
        border-radius: 8px;
        font-weight: 600;
    }
</style>
""", unsafe_allow_html=True)


# Initialize Paths & Engine Instances
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DATA_DIR = os.path.join(BASE_DIR, "data")
SKILL_GRAPH_PATH = os.path.join(DATA_DIR, "skill_graph.json")
COURSES_PATH = os.path.join(DATA_DIR, "courses_dataset.json")
QUIZZES_PATH = os.path.join(DATA_DIR, "diagnostic_quizzes.json")


@st.cache_resource
def get_engines():
    """Initializes and caches KnowledgeGraph, Recommender, and AdaptiveEngine."""
    kg = KnowledgeGraph(SKILL_GRAPH_PATH)
    rec = ContentBasedRecommender(COURSES_PATH)
    adaptive = AdaptiveEngine(kg, rec)
    return kg, rec, adaptive


@st.cache_data
def load_diagnostic_data():
    """Loads diagnostic quiz data."""
    if os.path.exists(QUIZZES_PATH):
        with open(QUIZZES_PATH, "r", encoding="utf-8") as f:
            return json.load(f)
    return {}


kg, rec, adaptive_engine = get_engines()
diagnostic_quizzes_all = load_diagnostic_data()


# Session State Initialization
if "user_profile" not in st.session_state:
    st.session_state.user_profile = {
        "name": "Alex Smith",
        "domain": "Artificial Intelligence & Machine Learning",
        "experience_level": "Beginner",
        "hours_per_week": 10,
        "preferred_media": "Video Course",
        "interests": "Deep learning, neural networks, PyTorch"
    }

if "diagnostic_result" not in st.session_state:
    st.session_state.diagnostic_result = None

if "roadmap" not in st.session_state:
    st.session_state.roadmap = None

if "current_tab" not in st.session_state:
    st.session_state.current_tab = "🎯 Profile & Goals"


# ==========================================
# SIDEBAR
# ==========================================
with st.sidebar:
    st.image("https://img.icons8.com/isometric/100/graduation-cap.png", width=64)
    st.title("Learner Portal")
    st.caption("AI-Powered Adaptive Learning Engine")

    st.markdown("---")
    st.markdown("### Active Learner Profile")
    st.write(f"**Name:** {st.session_state.user_profile.get('name')}")
    st.write(f"**Track:** {st.session_state.user_profile.get('domain')}")
    st.write(f"**Level:** {st.session_state.user_profile.get('experience_level')}")
    st.write(f"**Commitment:** {st.session_state.user_profile.get('hours_per_week')} hrs/week")

    if st.session_state.roadmap:
        progress = st.session_state.roadmap.get("overall_progress", 0.0)
        st.markdown("---")
        st.markdown(f"**Overall Progress: {progress}%**")
        st.progress(progress / 100.0)

    st.markdown("---")
    st.markdown("### Profile Management")
    saved_users = list_saved_users()
    if saved_users:
        selected_user = st.selectbox("Load Saved Student Profile", ["-- Select --"] + saved_users)
        if st.button("Load Profile") and selected_user != "-- Select --":
            saved_data = load_user_state(selected_user)
            if saved_data:
                st.session_state.user_profile = saved_data.get("profile", st.session_state.user_profile)
                st.session_state.roadmap = saved_data.get("roadmap", st.session_state.roadmap)
                st.success(f"Loaded profile for {selected_user}!")
                st.rerun()

    if st.session_state.roadmap:
        if st.button("Save Current Progress"):
            uname = st.session_state.user_profile.get("name", "Student")
            save_user_state(uname, st.session_state.user_profile, st.session_state.roadmap)
            st.success(f"Progress saved for {uname}!")


# ==========================================
# MAIN HEADER
# ==========================================
st.markdown('<div class="main-title">AI-Based Personalized Learning Path Recommendation System</div>', unsafe_allow_html=True)
st.markdown(
    '<div class="sub-title">Dynamically constructs customized curricula using <b>Knowledge Graph DAGs</b>, <b>Content-Based Vector Similarity</b>, and <b>Adaptive Diagnostic Feedback</b>.</div>',
    unsafe_allow_html=True
)

tab1, tab2, tab3, tab4, tab5 = st.tabs([
    "🎯 1. Profile & Goals",
    "📝 2. Diagnostic Assessment",
    "🗺️ 3. Personalized Roadmap",
    "📊 4. Skill Mastery & Graph",
    "📄 5. Project Synopsis & Export"
])


# ==============================================================================
# TAB 1: USER PROFILING & GOAL SETTING
# ==============================================================================
with tab1:
    st.header("Step 1: Define Learner Profile & Goals")
    st.write("Configure your target career specialization, background experience, and weekly commitment.")

    col1, col2 = st.columns([1, 1])

    with col1:
        u_name = st.text_input("Full Name / Student ID", value=st.session_state.user_profile.get("name", "Alex Smith"))
        
        domain_list = kg.domains if kg.domains else [
            "Artificial Intelligence & Machine Learning",
            "Full-Stack Web Development",
            "Data Science & Analytics",
            "Cybersecurity Essentials",
            "Cloud Computing & DevOps"
        ]
        
        curr_domain_idx = 0
        if st.session_state.user_profile.get("domain") in domain_list:
            curr_domain_idx = domain_list.index(st.session_state.user_profile.get("domain"))
            
        u_domain = st.selectbox("Target Career Track / Specialization", domain_list, index=curr_domain_idx)

        levels = ["Beginner", "Intermediate", "Advanced"]
        curr_lvl_idx = levels.index(st.session_state.user_profile.get("experience_level", "Beginner"))
        u_level = st.selectbox("Self-Assessed Baseline Experience", levels, index=curr_lvl_idx)

    with col2:
        u_hours = st.slider(
            "Weekly Study Commitment (Hours)",
            min_value=2,
            max_value=30,
            value=int(st.session_state.user_profile.get("hours_per_week", 10)),
            step=1,
            help="The engine divides topics into weekly sprints based on this commitment."
        )

        media_options = ["Video Course", "Interactive / Hands-on", "Documentation & Articles", "Guided Project"]
        curr_media_idx = 0
        if st.session_state.user_profile.get("preferred_media") in media_options:
            curr_media_idx = media_options.index(st.session_state.user_profile.get("preferred_media"))
            
        u_media = st.selectbox("Preferred Primary Learning Style", media_options, index=curr_media_idx)

        u_interests = st.text_area(
            "Key Focus Topics / Aspirations (For AI Content Matching)",
            value=st.session_state.user_profile.get("interests", "PyTorch, deep neural networks, transformer architectures"),
            help="Keywords used by the TF-IDF / Cosine Similarity engine to rank course materials."
        )

    if st.button("Save Profile & Proceed to Diagnostic Assessment ➡️", type="primary"):
        st.session_state.user_profile = {
            "name": u_name,
            "domain": u_domain,
            "experience_level": u_level,
            "hours_per_week": u_hours,
            "preferred_media": u_media,
            "interests": u_interests
        }
        st.success("Profile saved successfully! Switch to Tab 2 to take your diagnostic evaluation.")


# ==============================================================================
# TAB 2: DIAGNOSTIC ASSESSMENT & SKILL GAP ANALYSIS
# ==============================================================================
with tab2:
    st.header("Step 2: Diagnostic Assessment (Skill Gap Analysis)")
    st.write(
        "Take this quick diagnostic quiz to verify your baseline knowledge. The AI engine analyzes your responses "
        "to fast-track topics you've already mastered and schedule remedial foundations for weak areas."
    )

    active_domain = st.session_state.user_profile.get("domain", "Artificial Intelligence & Machine Learning")
    quiz_questions = diagnostic_quizzes_all.get(active_domain, [])

    if not quiz_questions:
        st.warning(f"No specific diagnostic questions found for {active_domain}. Standard default tracks will be used.")
    else:
        st.info(f"Target Domain: **{active_domain}** ({len(quiz_questions)} Questions)")

        quiz_form = st.form(key="diag_quiz_form")
        user_answers = {}

        for i, q in enumerate(quiz_questions, start=1):
            with quiz_form:
                st.markdown(f"**Q{i}: {q['question']}** &nbsp; `<span class='badge-primary'>{q.get('difficulty')}</span>`", unsafe_allow_html=True)
                choice = st.radio(
                    f"Select answer for Q{i}:",
                    options=range(len(q["options"])),
                    format_func=lambda x, opts=q["options"]: opts[x],
                    key=f"diag_{q['id']}",
                    label_visibility="collapsed"
                )
                user_answers[q["id"]] = choice
                st.markdown("---")

        submitted = quiz_form.form_submit_button("Submit Diagnostic Evaluation 🚀", type="primary")

        if submitted:
            result = adaptive_engine.evaluate_diagnostic(active_domain, user_answers, quiz_questions)
            st.session_state.diagnostic_result = result

            # Auto-generate or update the roadmap
            st.session_state.roadmap = adaptive_engine.generate_roadmap(
                st.session_state.user_profile,
                result
            )

        if st.session_state.diagnostic_result:
            res = st.session_state.diagnostic_result
            pct = res["percentage"]
            score = res["score"]
            total = res["total"]

            st.markdown("### 📊 Diagnostic Evaluation Results")
            c_score1, c_score2, c_score3 = st.columns(3)
            c_score1.metric("Score", f"{score} / {total}")
            c_score2.metric("Proficiency", f"{pct}%")
            c_score3.metric("AI Recommended Level", res["suggested_level"])

            if pct >= 80:
                st.success("🎉 Outstanding baseline knowledge! Foundational topics will be fast-tracked to save you study time.")
            elif pct >= 50:
                st.info("👍 Solid foundations detected! Roadmap balanced with foundational reviews and core intermediate milestones.")
            else:
                st.warning("⚠️ Prerequisite gaps identified. Foundational and remedial prerequisite modules will be prioritized.")

            with st.expander("Review Detailed Question Explanations"):
                for q in quiz_questions:
                    qid = q["id"]
                    q_info = res["topic_results"].get(qid, {})
                    is_correct = q_info.get("correct", False)
                    status_icon = "✅ Correct" if is_correct else "❌ Incorrect"
                    st.markdown(f"**{q['question']}** — {status_icon}")
                    st.caption(f"**Explanation:** {q['explanation']}")
                    st.markdown("---")

            st.write("👉 Your personalized roadmap has been generated! View it in **Tab 3: Personalized Roadmap**.")


# ==============================================================================
# TAB 3: PERSONALIZED ADAPTIVE ROADMAP
# ==============================================================================
with tab3:
    st.header("Step 3: Adaptive Personalized Learning Roadmap")

    # Generate roadmap if not yet generated
    if not st.session_state.roadmap:
        if st.button("Generate Learning Path Now ⚡", type="primary"):
            st.session_state.roadmap = adaptive_engine.generate_roadmap(
                st.session_state.user_profile,
                st.session_state.diagnostic_result
            )
            st.rerun()

    roadmap = st.session_state.roadmap

    if roadmap:
        # Top Metrics Bar
        m_col1, m_col2, m_col3, m_col4 = st.columns([1, 1, 1, 1.2])
        m_col1.metric("Total Duration", f"{roadmap.get('total_weeks', 0)} Weeks")
        m_col2.metric("Total Hours", f"{roadmap.get('total_estimated_hours', 0)} Hours")
        m_col3.metric("Pace", f"{roadmap.get('hours_per_week', 10)} hrs/week")
        with m_col4:
            gauge = create_progress_gauge(roadmap.get("overall_progress", 0.0))
            if gauge:
                st.plotly_chart(gauge, use_container_width=True)

        st.markdown("---")

        # Interactive Gantt Timeline
        st.subheader("🗓️ Syllabus Timeline")
        timeline_fig = create_timeline_chart(roadmap.get("milestones", []))
        if timeline_fig:
            st.plotly_chart(timeline_fig, use_container_width=True)

        st.markdown("---")
        st.subheader("📚 Milestone Modules & Curated Resources")

        milestones = roadmap.get("milestones", [])
        for idx, m in enumerate(milestones):
            status = m.get("status", "Not Started")
            badge_class = {
                "Mastered (Honors)": "badge-success",
                "Completed": "badge-primary",
                "Fast-Tracked": "badge-primary",
                "Needs Remediation": "badge-danger",
                "Not Started": "badge-warning"
            }.get(status, "badge-warning")

            with st.expander(
                f"Milestone {idx+1}: {m['topic_name']} (Weeks {m['start_week']}-{m['end_week']}) — Status: {status}",
                expanded=(idx == 0 or status in ["Needs Remediation", "Not Started"])
            ):
                st.markdown(f"<span class='{badge_class}'>{status}</span> &nbsp; <b>Category:</b> {m.get('category')} | <b>Difficulty:</b> {m.get('difficulty')} | <b>Estimated Hours:</b> {m.get('estimated_hours')}h", unsafe_allow_html=True)
                st.write(m.get("description", ""))

                if m.get("notes"):
                    st.info(f"💡 **Adaptive Note:** {m['notes']}")

                # Show prerequisite links
                prereqs = m.get("prerequisites", [])
                if prereqs:
                    st.caption(f"🔗 **Prerequisites:** {', '.join(prereqs)}")
                else:
                    st.caption("🔗 **Prerequisites:** None (Core Entry Foundation)")

                # Show Recommended Resources
                st.markdown("#### Curated Resources (Ranked by AI Match Score):")
                resources = m.get("recommended_resources", [])
                if resources:
                    r_cols = st.columns(min(len(resources), 2))
                    for c_idx, res in enumerate(resources[:2]):
                        with r_cols[c_idx]:
                            st.markdown(f"""
                            <div class="card-box">
                                <h5><a href="{res.get('url')}" target="_blank" style="text-decoration:none; color:#1E3A8A;">{res.get('title')}</a></h5>
                                <p style="font-size:0.85rem; color:#6B7280; margin-bottom:0.4rem;">
                                    <b>{res.get('provider')}</b> • {res.get('media_type')} • {res.get('duration_hours')} hrs • ⭐ {res.get('rating')}
                                </p>
                                <p style="font-size:0.9rem; color:#374151;">{res.get('summary')}</p>
                                <a href="{res.get('url')}" target="_blank" style="display:inline-block; font-size:0.85rem; font-weight:600; color:#4F46E5;">Open Resource ↗</a>
                            </div>
                            """, unsafe_allow_html=True)

                # Milestone Quiz & Adaptation Trigger
                st.markdown("#### 🎯 Milestone Mastery Verification:")
                q_col1, q_col2 = st.columns([2, 1])
                with q_col1:
                    test_score = st.slider(
                        f"Enter Milestone Assessment Score (%) for {m['topic_name']}",
                        min_value=0,
                        max_value=100,
                        value=int(m.get("quiz_score") or 75),
                        key=f"slider_{m['milestone_id']}"
                    )
                with q_col2:
                    st.write("")
                    st.write("")
                    if st.button(f"Update & Adapt Milestone", key=f"btn_{m['milestone_id']}"):
                        st.session_state.roadmap = adaptive_engine.adapt_milestone(
                            st.session_state.roadmap,
                            m["milestone_id"],
                            test_score
                        )
                        st.success(f"Milestone updated with score {test_score}%! Roadmap adapted.")
                        st.rerun()
    else:
        st.info("Please set up your profile in Tab 1 and click 'Generate Learning Path' to begin.")


# ==============================================================================
# TAB 4: SKILL MASTERY & KNOWLEDGE GRAPH
# ==============================================================================
with tab4:
    st.header("Step 4: Skill Mastery Analytics & Prerequisite Graph")

    if st.session_state.roadmap:
        analytics_col1, analytics_col2 = st.columns([1.2, 1])

        with analytics_col1:
            st.subheader("🕸️ Competency Radar Chart")
            st.caption("Visualizes current mastery across all pedagogical domains in this specialization.")
            mastery_dict = adaptive_engine.calculate_skill_mastery(st.session_state.roadmap)
            radar = create_radar_chart(mastery_dict)
            if radar:
                st.plotly_chart(radar, use_container_width=True)
            else:
                st.table(pd.DataFrame(list(mastery_dict.items()), columns=["Category", "Mastery %"]))

        with analytics_col2:
            st.subheader("📈 Category Mastery Breakdown")
            for cat, val in mastery_dict.items():
                st.write(f"**{cat}**")
                st.progress(val / 100.0)
                st.caption(f"Mastery: {val}%")

        st.markdown("---")
        st.subheader("🌳 Directed Acyclic Graph (DAG) Prerequisite Hierarchy")
        st.caption("Topological sequencing guarantees foundational nodes are satisfied before unlocking advanced modules.")

        active_domain = st.session_state.user_profile.get("domain", "Artificial Intelligence & Machine Learning")
        graph_data = kg.get_graph_visualization_data(active_domain)

        table_data = []
        for n in graph_data["nodes"]:
            prereqs = kg.get_prerequisites(n["id"])
            table_data.append({
                "Node ID": n["id"],
                "Topic Name": n["label"],
                "Category": n["category"],
                "Difficulty": n["difficulty"],
                "Estimated Hours": n["hours"],
                "Direct Prerequisites": ", ".join(prereqs) if prereqs else "Root (None)"
            })
        st.dataframe(pd.DataFrame(table_data), use_container_width=True)
    else:
        st.info("Complete Tab 1 and generate your roadmap to view mastery analytics.")


# ==============================================================================
# TAB 5: PROJECT SYNOPSIS & EXPORT
# ==============================================================================
with tab5:
    st.header("Step 5: Project Synopsis & Export")
    st.write("Download your personalized study curriculum or review academic documentation for your project submission and viva.")

    if st.session_state.roadmap:
        export_md = generate_exportable_markdown(st.session_state.user_profile, st.session_state.roadmap)
        st.download_button(
            label="📥 Download Personalized Syllabus Report (.md)",
            data=export_md,
            file_name=f"learning_path_{st.session_state.user_profile.get('name', 'student')}.md",
            mime="text/markdown",
            type="primary"
        )

    with st.expander("📖 Academic Project Abstract & Viva Defense Points", expanded=True):
        st.markdown("""
        ### Abstract
        The **AI-Based Personalized Learning Path Recommendation System** addresses the critical limitation of traditional, static e-learning platforms: one-size-fits-all curricula that disregard a learner's baseline competence, unique career goals, and cognitive learning preferences. 

        By formulating the learning syllabus as a **Directed Acyclic Graph (DAG)**, the system mathematically ensures prerequisite consistency through **Kahn's Topological Sorting Algorithm**. Course materials are dynamically ranked and recommended using a **Content-Based Vector Space Model (TF-IDF with Cosine Similarity)**, aligning topic tags and media types with student preferences. An **Adaptive Feedback Engine** continuously monitors diagnostic and milestone assessments, dynamically branching the curriculum by scheduling remedial micro-lessons for struggling concepts or accelerating proficient students.

        ---

        ### Key Technical Highlights for Viva / Project Review:
        1. **Pedagogical Knowledge Graph (DAG):** Prevents prerequisite circularity and guarantees strict logical topic progression ($G = (V, E)$).
        2. **Content-Based AI Recommender:** Employs TF-IDF Vectorization:
           $$\\text{Cosine Similarity}(u, c) = \\frac{\\vec{u} \\cdot \\vec{c}}{\\|\\vec{u}\\| \\|\\vec{c}\\|}$$
           augmented with multi-criteria bonuses for media format preference, difficulty matching, and ratings.
        3. **Dynamic Closed-Loop Adaptation:** Evaluates milestone quiz performances ($S_m$).
           - If $S_m < 60\\%$: Injects targeted remedial modules ($R_{rem}$) and increases sprint duration.
           - If $S_m \\ge 85\\%$: Honors mastery badge and accelerates sprint timeline.
        4. **Interactive Dashboard:** Complete visual progress indicators, Gantt schedule, and Plotly skill radar charts.
        """)
