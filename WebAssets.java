package com.learningpath.web;

public class WebAssets {

    public static String getIndexHtml() {
        return "<!DOCTYPE html>\n" +
"<html lang=\"en\">\n" +
"<head>\n" +
"    <meta charset=\"UTF-8\">\n" +
"    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
"    <title>AI Personalized Learning Path Recommender (Java Edition)</title>\n" +
"    <script src=\"https://cdn.tailwindcss.com\"></script>\n" +
"    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n" +
"    <link href=\"https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap\" rel=\"stylesheet\">\n" +
"    <style>\n" +
"        body { font-family: 'Inter', sans-serif; background-color: #F8FAFC; }\n" +
"        .gradient-text { background: linear-gradient(135deg, #4F46E5, #06B6D4); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }\n" +
"        .tab-btn.active { border-bottom: 3px solid #4F46E5; color: #4F46E5; font-weight: 600; }\n" +
"        .milestone-card { transition: all 0.2s ease-in-out; }\n" +
"        .milestone-card:hover { transform: translateY(-2px); box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.08); }\n" +
"    </style>\n" +
"</head>\n" +
"<body class=\"text-slate-800\">\n" +
"\n" +
"    <!-- Top Header -->\n" +
"    <header class=\"bg-white border-b border-slate-200 sticky top-0 z-50 shadow-sm\">\n" +
"        <div class=\"max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between\">\n" +
"            <div class=\"flex items-center space-x-3\">\n" +
"                <div class=\"w-10 h-10 rounded-xl bg-indigo-600 text-white flex items-center justify-center font-bold text-xl shadow-md\">\n" +
"                    AI\n" +
"                </div>\n" +
"                <div>\n" +
"                    <h1 class=\"text-lg font-bold gradient-text\">Personalized Learning Path Recommendation System</h1>\n" +
"                    <p class=\"text-xs text-slate-500\">Pure Java Backend • DAG Prerequisite Engine • TF-IDF Vector Recommender</p>\n" +
"                </div>\n" +
"            </div>\n" +
"            <div class=\"flex items-center space-x-3\">\n" +
"                <span class=\"px-2.5 py-1 text-xs font-semibold bg-emerald-50 text-emerald-700 rounded-full border border-emerald-200\">Java 17+ HTTP Server Active</span>\n" +
"                <span id=\"header-user-badge\" class=\"text-xs font-medium text-slate-600 bg-slate-100 px-3 py-1 rounded-full\">Alex Smith</span>\n" +
"            </div>\n" +
"        </div>\n" +
"    </header>\n" +
"\n" +
"    <!-- Main Tabs Navigation -->\n" +
"    <div class=\"max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 mt-6\">\n" +
"        <div class=\"flex border-b border-slate-200 space-x-8 text-sm font-medium text-slate-500 overflow-x-auto\">\n" +
"            <button onclick=\"switchTab('profile')\" id=\"tab-btn-profile\" class=\"tab-btn active pb-3 px-1 flex items-center space-x-2\">\n" +
"                <span>🎯</span><span>1. Profile & Goals</span>\n" +
"            </button>\n" +
"            <button onclick=\"switchTab('diagnostic')\" id=\"tab-btn-diagnostic\" class=\"tab-btn pb-3 px-1 flex items-center space-x-2\">\n" +
"                <span>📝</span><span>2. Diagnostic Quiz</span>\n" +
"            </button>\n" +
"            <button onclick=\"switchTab('roadmap')\" id=\"tab-btn-roadmap\" class=\"tab-btn pb-3 px-1 flex items-center space-x-2\">\n" +
"                <span>🗺️</span><span>3. Adaptive Roadmap</span>\n" +
"            </button>\n" +
"            <button onclick=\"switchTab('analytics')\" id=\"tab-btn-analytics\" class=\"tab-btn pb-3 px-1 flex items-center space-x-2\">\n" +
"                <span>📊</span><span>4. Skill Analytics</span>\n" +
"            </button>\n" +
"            <button onclick=\"switchTab('export')\" id=\"tab-btn-export\" class=\"tab-btn pb-3 px-1 flex items-center space-x-2\">\n" +
"                <span>📄</span><span>5. Academic Synopsis</span>\n" +
"            </button>\n" +
"        </div>\n" +
"    </div>\n" +
"\n" +
"    <!-- Main Content Area -->\n" +
"    <main class=\"max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6\">\n" +
"\n" +
"        <!-- TAB 1: PROFILE & GOALS -->\n" +
"        <section id=\"tab-profile\" class=\"space-y-6\">\n" +
"            <div class=\"bg-white p-6 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                <h2 class=\"text-xl font-bold text-slate-900 mb-1\">Step 1: Configure Your Learner Profile</h2>\n" +
"                <p class=\"text-sm text-slate-500 mb-6\">Define your career goal, weekly time availability, and learning preferences for the AI recommendation engine.</p>\n" +
"                \n" +
"                <div class=\"grid grid-cols-1 md:grid-cols-2 gap-6\">\n" +
"                    <div>\n" +
"                        <label class=\"block text-xs font-semibold text-slate-700 uppercase mb-2\">Student Name / ID</label>\n" +
"                        <input type=\"text\" id=\"input-name\" value=\"Alex Smith\" class=\"w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none\">\n" +
"                    </div>\n" +
"                    <div>\n" +
"                        <label class=\"block text-xs font-semibold text-slate-700 uppercase mb-2\">Target Career Specialization</label>\n" +
"                        <select id=\"input-domain\" class=\"w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none\">\n" +
"                            <option value=\"Artificial Intelligence & Machine Learning\">Artificial Intelligence & Machine Learning</option>\n" +
"                            <option value=\"Full-Stack Web Development\">Full-Stack Web Development</option>\n" +
"                            <option value=\"Data Science & Analytics\">Data Science & Analytics</option>\n" +
"                            <option value=\"Cybersecurity Essentials\">Cybersecurity Essentials</option>\n" +
"                            <option value=\"Cloud Computing & DevOps\">Cloud Computing & DevOps</option>\n" +
"                        </select>\n" +
"                    </div>\n" +
"                    <div>\n" +
"                        <label class=\"block text-xs font-semibold text-slate-700 uppercase mb-2\">Weekly Commitment (Hours: <span id=\"val-hours\" class=\"text-indigo-600 font-bold\">10</span> hrs/week)</label>\n" +
"                        <input type=\"range\" id=\"input-hours\" min=\"2\" max=\"30\" value=\"10\" oninput=\"document.getElementById('val-hours').innerText=this.value\" class=\"w-full accent-indigo-600\">\n" +
"                    </div>\n" +
"                    <div>\n" +
"                        <label class=\"block text-xs font-semibold text-slate-700 uppercase mb-2\">Preferred Learning Media Format</label>\n" +
"                        <select id=\"input-media\" class=\"w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none\">\n" +
"                            <option value=\"Video Course\">Video Course (e.g. YouTube, Coursera)</option>\n" +
"                            <option value=\"Interactive / Hands-on\">Interactive / Hands-on Coding (e.g. Kaggle, Exercism)</option>\n" +
"                            <option value=\"Documentation & Articles\">Official Documentation & Deep Dives</option>\n" +
"                            <option value=\"Guided Project\">Guided Capstone Projects</option>\n" +
"                        </select>\n" +
"                    </div>\n" +
"                    <div>\n" +
"                        <label class=\"block text-xs font-semibold text-slate-700 uppercase mb-2\">Self-Assessed Baseline Level</label>\n" +
"                        <select id=\"input-level\" class=\"w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none\">\n" +
"                            <option value=\"Beginner\">Beginner (New to the field)</option>\n" +
"                            <option value=\"Intermediate\">Intermediate (Some programming/math experience)</option>\n" +
"                            <option value=\"Advanced\">Advanced (Experienced looking to specialize)</option>\n" +
"                        </select>\n" +
"                    </div>\n" +
"                    <div>\n" +
"                        <label class=\"block text-xs font-semibold text-slate-700 uppercase mb-2\">Key Aspirations / Topics of Interest</label>\n" +
"                        <input type=\"text\" id=\"input-interests\" value=\"PyTorch, deep neural networks, transformer models, LLMs\" class=\"w-full px-4 py-2.5 rounded-lg border border-slate-300 text-sm focus:ring-2 focus:ring-indigo-500 focus:outline-none\">\n" +
"                    </div>\n" +
"                </div>\n" +
"\n" +
"                <div class=\"mt-8 flex justify-end\">\n" +
"                    <button onclick=\"saveProfileAndNext()\" class=\"bg-indigo-600 hover:bg-indigo-700 text-white font-semibold px-6 py-2.5 rounded-xl shadow-md transition-all flex items-center space-x-2\">\n" +
"                        <span>Save Profile & Start Diagnostic Assessment</span>\n" +
"                        <span>&rarr;</span>\n" +
"                    </button>\n" +
"                </div>\n" +
"            </div>\n" +
"        </section>\n" +
"\n" +
"        <!-- TAB 2: DIAGNOSTIC QUIZ -->\n" +
"        <section id=\"tab-diagnostic\" class=\"space-y-6 hidden\">\n" +
"            <div class=\"bg-white p-6 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                <div class=\"flex justify-between items-center mb-4\">\n" +
"                    <div>\n" +
"                        <h2 class=\"text-xl font-bold text-slate-900\">Step 2: Diagnostic Skill Gap Assessment</h2>\n" +
"                        <p class=\"text-sm text-slate-500\">Verify your baseline competence. The Java Adaptive Engine analyzes answers to fast-track known concepts and reinforce weak foundations.</p>\n" +
"                    </div>\n" +
"                    <span id=\"diag-track-badge\" class=\"px-3 py-1 bg-indigo-50 text-indigo-700 text-xs font-bold rounded-lg border border-indigo-200\">AI Track</span>\n" +
"                </div>\n" +
"\n" +
"                <div id=\"diagnostic-container\" class=\"space-y-6 mt-6\">\n" +
"                    <!-- Injected dynamically via JS -->\n" +
"                </div>\n" +
"\n" +
"                <div class=\"mt-8 flex justify-end space-x-4\">\n" +
"                    <button onclick=\"submitDiagnostic()\" class=\"bg-indigo-600 hover:bg-indigo-700 text-white font-semibold px-6 py-2.5 rounded-xl shadow-md transition-all\">\n" +
"                        Submit Diagnostic Evaluation 🚀\n" +
"                    </button>\n" +
"                </div>\n" +
"\n" +
"                <!-- Diagnostic Evaluation Results Card -->\n" +
"                <div id=\"diagnostic-results-card\" class=\"hidden mt-8 p-6 bg-slate-50 border border-slate-200 rounded-xl\">\n" +
"                    <h3 class=\"text-lg font-bold text-slate-900 mb-4\">Evaluation Results & Skill Gap Analysis</h3>\n" +
"                    <div class=\"grid grid-cols-1 sm:grid-cols-3 gap-4 mb-4 text-center\">\n" +
"                        <div class=\"bg-white p-4 rounded-xl border border-slate-200\">\n" +
"                            <p class=\"text-xs text-slate-500 uppercase font-semibold\">Score</p>\n" +
"                            <p id=\"diag-score-val\" class=\"text-2xl font-bold text-indigo-600\">-</p>\n" +
"                        </div>\n" +
"                        <div class=\"bg-white p-4 rounded-xl border border-slate-200\">\n" +
"                            <p class=\"text-xs text-slate-500 uppercase font-semibold\">Proficiency</p>\n" +
"                            <p id=\"diag-pct-val\" class=\"text-2xl font-bold text-indigo-600\">-</p>\n" +
"                        </div>\n" +
"                        <div class=\"bg-white p-4 rounded-xl border border-slate-200\">\n" +
"                            <p class=\"text-xs text-slate-500 uppercase font-semibold\">AI Verified Level</p>\n" +
"                            <p id=\"diag-level-val\" class=\"text-2xl font-bold text-emerald-600\">-</p>\n" +
"                        </div>\n" +
"                    </div>\n" +
"                    <div id=\"diag-feedback-msg\" class=\"text-sm text-slate-700 font-medium mb-4 p-3 bg-white rounded-lg border border-slate-200\"></div>\n" +
"                    <button onclick=\"switchTab('roadmap')\" class=\"bg-emerald-600 hover:bg-emerald-700 text-white font-semibold px-6 py-2 rounded-xl shadow-md\">\n" +
"                        View Personalized Roadmap &rarr;\n" +
"                    </button>\n" +
"                </div>\n" +
"            </div>\n" +
"        </section>\n" +
"\n" +
"        <!-- TAB 3: ADAPTIVE ROADMAP -->\n" +
"        <section id=\"tab-roadmap\" class=\"space-y-6 hidden\">\n" +
"            <!-- Metrics Summary Bar -->\n" +
"            <div class=\"grid grid-cols-2 md:grid-cols-4 gap-4\">\n" +
"                <div class=\"bg-white p-5 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                    <p class=\"text-xs font-semibold text-slate-500 uppercase\">Total Duration</p>\n" +
"                    <p id=\"metric-weeks\" class=\"text-2xl font-extrabold text-slate-900 mt-1\">- Weeks</p>\n" +
"                </div>\n" +
"                <div class=\"bg-white p-5 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                    <p class=\"text-xs font-semibold text-slate-500 uppercase\">Estimated Hours</p>\n" +
"                    <p id=\"metric-hours\" class=\"text-2xl font-extrabold text-slate-900 mt-1\">- Hours</p>\n" +
"                </div>\n" +
"                <div class=\"bg-white p-5 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                    <p class=\"text-xs font-semibold text-slate-500 uppercase\">Weekly Pace</p>\n" +
"                    <p id=\"metric-pace\" class=\"text-2xl font-extrabold text-indigo-600 mt-1\">10 hrs/wk</p>\n" +
"                </div>\n" +
"                <div class=\"bg-white p-5 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                    <p class=\"text-xs font-semibold text-slate-500 uppercase\">Completion Progress</p>\n" +
"                    <p id=\"metric-progress\" class=\"text-2xl font-extrabold text-emerald-600 mt-1\">0%</p>\n" +
"                </div>\n" +
"            </div>\n" +
"\n" +
"            <!-- Milestone Cards List -->\n" +
"            <div class=\"bg-white p-6 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                <div class=\"flex justify-between items-center mb-6\">\n" +
"                    <div>\n" +
"                        <h3 class=\"text-xl font-bold text-slate-900\">Personalized Milestones (Kahn's DAG Ordered)</h3>\n" +
"                        <p class=\"text-sm text-slate-500\">Prerequisite sequencing mathematically enforces that foundational topics are satisfied before advanced modules.</p>\n" +
"                    </div>\n" +
"                    <button onclick=\"generateRoadmap()\" class=\"px-4 py-2 text-xs font-bold text-indigo-600 bg-indigo-50 hover:bg-indigo-100 rounded-lg border border-indigo-200\">\n" +
"                        🔄 Regenerate Learning Path\n" +
"                    </button>\n" +
"                </div>\n" +
"\n" +
"                <div id=\"milestones-container\" class=\"space-y-6\">\n" +
"                    <!-- Dynamic Milestones Injected Here -->\n" +
"                </div>\n" +
"            </div>\n" +
"        </section>\n" +
"\n" +
"        <!-- TAB 4: SKILL ANALYTICS -->\n" +
"        <section id=\"tab-analytics\" class=\"space-y-6 hidden\">\n" +
"            <div class=\"grid grid-cols-1 md:grid-cols-2 gap-6\">\n" +
"                <div class=\"bg-white p-6 rounded-2xl border border-slate-200 shadow-sm flex flex-col items-center\">\n" +
"                    <h3 class=\"text-lg font-bold text-slate-900 mb-2\">Competency Radar Chart</h3>\n" +
"                    <p class=\"text-xs text-slate-500 mb-4 text-center\">Visualizes student mastery across curriculum pedagogical dimensions.</p>\n" +
"                    <div class=\"w-full max-w-md\">\n" +
"                        <canvas id=\"radarChartCanvas\"></canvas>\n" +
"                    </div>\n" +
"                </div>\n" +
"                <div class=\"bg-white p-6 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                    <h3 class=\"text-lg font-bold text-slate-900 mb-2\">Category Mastery Index</h3>\n" +
"                    <p class=\"text-xs text-slate-500 mb-6\">Real-time mastery derived from diagnostic tests and milestone quiz verifications.</p>\n" +
"                    <div id=\"category-bars\" class=\"space-y-4\">\n" +
"                        <!-- Category bars injected here -->\n" +
"                    </div>\n" +
"                </div>\n" +
"            </div>\n" +
"        </section>\n" +
"\n" +
"        <!-- TAB 5: ACADEMIC SYNOPSIS & EXPORT -->\n" +
"        <section id=\"tab-export\" class=\"space-y-6 hidden\">\n" +
"            <div class=\"bg-white p-6 rounded-2xl border border-slate-200 shadow-sm\">\n" +
"                <div class=\"flex justify-between items-center mb-4\">\n" +
"                    <div>\n" +
"                        <h3 class=\"text-xl font-bold text-slate-900\">Academic Synopsis & Viva Defense Reference</h3>\n" +
"                        <p class=\"text-sm text-slate-500\">Formal project abstract, algorithmic formulation, and review Q&A.</p>\n" +
"                    </div>\n" +
"                    <button onclick=\"downloadMarkdown()\" class=\"bg-indigo-600 hover:bg-indigo-700 text-white text-xs font-bold px-4 py-2.5 rounded-lg shadow-sm flex items-center space-x-1.5\">\n" +
"                        <span>📥 Download Syllabus Report (.md)</span>\n" +
"                    </button>\n" +
"                </div>\n" +
"\n" +
"                <div class=\"prose prose-slate max-w-none text-sm space-y-4 border-t border-slate-100 pt-4\">\n" +
"                    <h4 class=\"font-bold text-slate-900 text-base\">1. Abstract & Problem Statement</h4>\n" +
"                    <p class=\"text-slate-600\">Traditional online learning platforms force students into uniform linear tracks regardless of prior mastery or cognitive pacing. This Java-engineered recommendation platform formulates the curriculum as a <b>Directed Acyclic Graph (DAG)</b>, using <b>Kahn's Topological Sorting Algorithm</b> to guarantee prerequisite consistency. It integrates <b>TF-IDF Vector Space Modeling</b> with <b>Cosine Similarity</b> to score course modules, and incorporates an <b>Adaptive Closed-Loop Feedback Engine</b> that branches the learning path dynamically based on quiz performance.</p>\n" +
"\n" +
"                    <h4 class=\"font-bold text-slate-900 text-base\">2. Algorithmic Formulation</h4>\n" +
"                    <div class=\"p-4 bg-slate-50 rounded-xl font-mono text-xs text-slate-700 space-y-2\">\n" +
"                        <p><b>A. DAG Prerequisite Ordering:</b> In-degree calculation; nodes with in_degree=0 scheduled iteratively.</p>\n" +
"                        <p><b>B. TF-IDF Cosine Similarity:</b> Score = CosineSim(UserVector, CourseVector) + MediaBonus(0.25) + DiffBonus(0.15) + RatingBonus.</p>\n" +
"                        <p><b>C. Adaptive Remediation:</b> If QuizScore &lt; 60% &rarr; Status='Needs Remediation', RemedialCourse added, Hours += 4.</p>\n" +
"                    </div>\n" +
"\n" +
"                    <h4 class=\"font-bold text-slate-900 text-base\">3. Key Viva Voce Questions & Answers</h4>\n" +
"                    <div class=\"space-y-3\">\n" +
"                        <div class=\"p-3 bg-indigo-50/50 rounded-lg border border-indigo-100\">\n" +
"                            <p class=\"font-semibold text-indigo-900\">Q: Why pure Java without external machine learning libraries?</p>\n" +
"                            <p class=\"text-xs text-indigo-800 mt-1\">A: Building the TF-IDF vectorizer, cosine similarity engine, and Kahn's DAG algorithm in pure Java showcases complete mastery of fundamental data structures, graph algorithms, and Information Retrieval concepts from scratch with zero dependency bloat.</p>\n" +
"                        </div>\n" +
"                        <div class=\"p-3 bg-indigo-50/50 rounded-lg border border-indigo-100\">\n" +
"                            <p class=\"font-semibold text-indigo-900\">Q: How does the system resolve circular prerequisite deadlocks?</p>\n" +
"                            <p class=\"text-xs text-indigo-800 mt-1\">A: Kahn's algorithm monitors graph cycle existence. If remaining in-degrees fail to reach zero, cycles are detected and unblocked to prevent deadlock.</p>\n" +
"                        </div>\n" +
"                    </div>\n" +
"                </div>\n" +
"            </div>\n" +
"        </section>\n" +
"\n" +
"    </main>\n" +
"\n" +
"    <script>\n" +
"        let currentProfile = {\n" +
"            name: 'Alex Smith',\n" +
"            domain: 'Artificial Intelligence & Machine Learning',\n" +
"            hoursPerWeek: 10,\n" +
"            preferredMedia: 'Video Course',\n" +
"            experienceLevel: 'Beginner',\n" +
"            interests: 'PyTorch, deep neural networks, transformer models, LLMs'\n" +
"        };\n" +
"\n" +
"        let currentDiagnostic = null;\n" +
"        let currentRoadmap = null;\n" +
"        let radarChart = null;\n" +
"\n" +
"        function switchTab(tabId) {\n" +
"            ['profile', 'diagnostic', 'roadmap', 'analytics', 'export'].forEach(t => {\n" +
"                document.getElementById('tab-' + t).classList.add('hidden');\n" +
"                document.getElementById('tab-btn-' + t).classList.remove('active');\n" +
"            });\n" +
"            document.getElementById('tab-' + tabId).classList.remove('hidden');\n" +
"            document.getElementById('tab-btn-' + tabId).classList.add('active');\n" +
"\n" +
"            if (tabId === 'diagnostic' && !document.getElementById('diagnostic-container').hasChildNodes()) {\n" +
"                loadDiagnosticQuestions();\n" +
"            }\n" +
"            if (tabId === 'analytics') {\n" +
"                renderAnalytics();\n" +
"            }\n" +
"        }\n" +
"\n" +
"        function saveProfileAndNext() {\n" +
"            currentProfile = {\n" +
"                name: document.getElementById('input-name').value || 'Learner',\n" +
"                domain: document.getElementById('input-domain').value,\n" +
"                hoursPerWeek: parseInt(document.getElementById('input-hours').value) || 10,\n" +
"                preferredMedia: document.getElementById('input-media').value,\n" +
"                experienceLevel: document.getElementById('input-level').value,\n" +
"                interests: document.getElementById('input-interests').value\n" +
"            };\n" +
"            document.getElementById('header-user-badge').innerText = currentProfile.name;\n" +
"            document.getElementById('diag-track-badge').innerText = currentProfile.domain;\n" +
"            loadDiagnosticQuestions();\n" +
"            switchTab('diagnostic');\n" +
"        }\n" +
"\n" +
"        async function loadDiagnosticQuestions() {\n" +
"            const res = await fetch('/api/diagnostic-questions?domain=' + encodeURIComponent(currentProfile.domain));\n" +
"            const questions = await res.json();\n" +
"            const container = document.getElementById('diagnostic-container');\n" +
"            container.innerHTML = '';\n" +
"\n" +
"            questions.forEach((q, idx) => {\n" +
"                let optHtml = '';\n" +
"                q.options.forEach((opt, oIdx) => {\n" +
"                    optHtml += `\n" +
"                        <label class=\"flex items-center space-x-3 p-3 rounded-lg border border-slate-200 hover:bg-indigo-50/50 cursor-pointer transition-all\">\n" +
"                            <input type=\"radio\" name=\"q_${q.id}\" value=\"${oIdx}\" class=\"accent-indigo-600\">\n" +
"                            <span class=\"text-sm text-slate-700\">${opt}</span>\n" +
"                        </label>\n" +
"                    `;\n" +
"                });\n" +
"\n" +
"                container.innerHTML += `\n" +
"                    <div class=\"p-5 rounded-xl border border-slate-200 bg-white shadow-sm space-y-3\">\n" +
"                        <div class=\"flex justify-between items-center\">\n" +
"                            <p class=\"font-semibold text-slate-900 text-sm\">Q${idx + 1}: ${q.questionText}</p>\n" +
"                            <span class=\"text-xs font-bold px-2 py-0.5 bg-slate-100 text-slate-700 rounded\">${q.difficulty}</span>\n" +
"                        </div>\n" +
"                        <div class=\"grid grid-cols-1 md:grid-cols-2 gap-2 mt-2\">\n" +
"                            ${optHtml}\n" +
"                        </div>\n" +
"                    </div>\n" +
"                `;\n" +
"            });\n" +
"        }\n" +
"\n" +
"        async function submitDiagnostic() {\n" +
"            const inputs = document.querySelectorAll('#diagnostic-container input[type=\"radio\"]:checked');\n" +
"            let answers = {};\n" +
"            inputs.forEach(inp => {\n" +
"                const qid = inp.name.replace('q_', '');\n" +
"                answers[qid] = parseInt(inp.value);\n" +
"            });\n" +
"\n" +
"            const res = await fetch('/api/evaluate-diagnostic', {\n" +
"                method: 'POST',\n" +
"                headers: { 'Content-Type': 'application/json' },\n" +
"                body: JSON.stringify({ domain: currentProfile.domain, answers: answers })\n" +
"            });\n" +
"            currentDiagnostic = await res.json();\n" +
"\n" +
"            document.getElementById('diagnostic-results-card').classList.remove('hidden');\n" +
"            document.getElementById('diag-score-val').innerText = `${currentDiagnostic.score} / ${currentDiagnostic.total}`;\n" +
"            document.getElementById('diag-pct-val').innerText = `${currentDiagnostic.percentage}%`;\n" +
"            document.getElementById('diag-level-val').innerText = currentDiagnostic.suggestedLevel;\n" +
"\n" +
"            let msg = '';\n" +
"            if (currentDiagnostic.percentage >= 80) {\n" +
"                msg = '🎉 Exceptional performance! Foundational modules are fast-tracked, saving you study hours.';\n" +
"            } else if (currentDiagnostic.percentage >= 50) {\n" +
"                msg = '👍 Solid baseline detected. Balanced curriculum structured around your target specialization.';\n" +
"            } else {\n" +
"                msg = '⚠️ Prerequisite blindspots identified. Foundational modules prioritized to guarantee mastery.';\n" +
"            }\n" +
"            document.getElementById('diag-feedback-msg').innerText = msg;\n" +
"\n" +
"            // Auto-generate roadmap with diagnostic results\n" +
"            generateRoadmap();\n" +
"        }\n" +
"\n" +
"        async function generateRoadmap() {\n" +
"            const payload = {\n" +
"                profile: currentProfile,\n" +
"                diagnostic: currentDiagnostic\n" +
"            };\n" +
"            const res = await fetch('/api/generate-roadmap', {\n" +
"                method: 'POST',\n" +
"                headers: { 'Content-Type': 'application/json' },\n" +
"                body: JSON.stringify(payload)\n" +
"            });\n" +
"            currentRoadmap = await res.json();\n" +
"            renderRoadmap();\n" +
"        }\n" +
"\n" +
"        function renderRoadmap() {\n" +
"            if (!currentRoadmap) return;\n" +
"            document.getElementById('metric-weeks').innerText = `${currentRoadmap.totalWeeks} Weeks`;\n" +
"            document.getElementById('metric-hours').innerText = `${currentRoadmap.totalEstimatedHours} Hours`;\n" +
"            document.getElementById('metric-pace').innerText = `${currentRoadmap.hoursPerWeek} hrs/wk`;\n" +
"            document.getElementById('metric-progress').innerText = `${currentRoadmap.overallProgress}%`;\n" +
"\n" +
"            const container = document.getElementById('milestones-container');\n" +
"            container.innerHTML = '';\n" +
"\n" +
"            currentRoadmap.milestones.forEach((m, idx) => {\n" +
"                let statusBadge = '';\n" +
"                if (m.status === 'Mastered (Honors)') {\n" +
"                    statusBadge = '<span class=\"px-2.5 py-1 text-xs font-bold rounded-full bg-emerald-100 text-emerald-800\">⭐ Mastered (Honors)</span>';\n" +
"                } else if (m.status === 'Needs Remediation') {\n" +
"                    statusBadge = '<span class=\"px-2.5 py-1 text-xs font-bold rounded-full bg-rose-100 text-rose-800\">⚠️ Needs Remediation</span>';\n" +
"                } else if (m.status === 'Fast-Tracked') {\n" +
"                    statusBadge = '<span class=\"px-2.5 py-1 text-xs font-bold rounded-full bg-indigo-100 text-indigo-800\">⚡ Fast-Tracked</span>';\n" +
"                } else if (m.status === 'Completed') {\n" +
"                    statusBadge = '<span class=\"px-2.5 py-1 text-xs font-bold rounded-full bg-blue-100 text-blue-800\">✅ Completed</span>';\n" +
"                } else {\n" +
"                    statusBadge = '<span class=\"px-2.5 py-1 text-xs font-bold rounded-full bg-slate-100 text-slate-700\">⏳ Upcoming</span>';\n" +
"                }\n" +
"\n" +
"                let coursesHtml = '';\n" +
"                m.recommendedResources.forEach(c => {\n" +
"                    coursesHtml += `\n" +
"                        <div class=\"p-4 rounded-xl border border-slate-200 bg-slate-50/60 hover:bg-slate-100/80 transition-all\">\n" +
"                            <div class=\"flex justify-between items-start\">\n" +
"                                <h5 class=\"font-bold text-sm text-slate-900\">${c.title}</h5>\n" +
"                                <span class=\"text-xs font-bold text-amber-600\">⭐ ${c.rating}</span>\n" +
"                            </div>\n" +
"                            <p class=\"text-xs text-slate-500 mt-0.5\">${c.provider} • ${c.mediaType} • ~${c.durationHours}h</p>\n" +
"                            <p class=\"text-xs text-slate-600 mt-2 line-clamp-2\">${c.summary}</p>\n" +
"                            <a href=\"${c.url}\" target=\"_blank\" class=\"inline-block mt-3 text-xs font-bold text-indigo-600 hover:text-indigo-800\">Open Learning Resource &rarr;</a>\n" +
"                        </div>\n" +
"                    `;\n" +
"                });\n" +
"\n" +
"                let prereqsText = m.prerequisites && m.prerequisites.length > 0 ? m.prerequisites.join(', ') : 'None (Root Foundation)';\n" +
"                let noteHtml = m.notes ? `<div class=\"p-3 bg-amber-50 border border-amber-200 rounded-lg text-xs text-amber-800 font-medium mb-3\">💡 ${m.notes}</div>` : '';\n" +
"\n" +
"                container.innerHTML += `\n" +
"                    <div class=\"milestone-card p-6 rounded-2xl border border-slate-200 bg-white shadow-sm space-y-4\">\n" +
"                        <div class=\"flex flex-wrap justify-between items-center gap-2\">\n" +
"                            <div>\n" +
"                                <div class=\"flex items-center space-x-2\">\n" +
"                                    <span class=\"text-xs font-extrabold text-indigo-600 uppercase tracking-wide\">Milestone ${idx + 1}</span>\n" +
"                                    <span class=\"text-xs text-slate-400\">•</span>\n" +
"                                    <span class=\"text-xs text-slate-500 font-medium\">Weeks ${m.startWeek} - ${m.endWeek} (${m.estimatedHours} Hours)</span>\n" +
"                                </div>\n" +
"                                <h4 class=\"text-base font-extrabold text-slate-900 mt-0.5\">${m.topicName}</h4>\n" +
"                            </div>\n" +
"                            <div class=\"flex items-center space-x-2\">\n" +
"                                ${statusBadge}\n" +
"                            </div>\n" +
"                        </div>\n" +
"\n" +
"                        <p class=\"text-xs text-slate-600\">${m.description}</p>\n" +
"                        <p class=\"text-xs text-slate-500\">🔗 <b>Prerequisites:</b> ${prereqsText}</p>\n" +
"                        ${noteHtml}\n" +
"\n" +
"                        <div class=\"grid grid-cols-1 md:grid-cols-2 gap-3 mt-3\">\n" +
"                            ${coursesHtml}\n" +
"                        </div>\n" +
"\n" +
"                        <!-- Interactive Milestone Quiz Slider -->\n" +
"                        <div class=\"pt-4 border-t border-slate-100 flex flex-wrap items-center justify-between gap-4\">\n" +
"                            <div class=\"flex items-center space-x-3\">\n" +
"                                <span class=\"text-xs font-semibold text-slate-700\">Verify Mastery Score:</span>\n" +
"                                <input type=\"range\" id=\"score_${m.milestoneId}\" min=\"0\" max=\"100\" value=\"${m.quizScore || 75}\" oninput=\"document.getElementById('val_${m.milestoneId}').innerText=this.value\" class=\"w-32 accent-indigo-600\">\n" +
"                                <span id=\"val_${m.milestoneId}\" class=\"text-xs font-bold text-indigo-600 w-8\">${m.quizScore || 75}%</span>\n" +
"                            </div>\n" +
"                            <button onclick=\"adaptMilestone('${m.milestoneId}')\" class=\"text-xs font-bold px-4 py-2 bg-indigo-50 hover:bg-indigo-100 text-indigo-700 rounded-xl border border-indigo-200 transition-all\">\n" +
"                                🎯 Update & Trigger Dynamic Adaptation\n" +
"                            </button>\n" +
"                        </div>\n" +
"                    </div>\n" +
"                `;\n" +
"            });\n" +
"        }\n" +
"\n" +
"        async function adaptMilestone(milestoneId) {\n" +
"            const score = parseFloat(document.getElementById('score_' + milestoneId).value);\n" +
"            const res = await fetch('/api/adapt-milestone', {\n" +
"                method: 'POST',\n" +
"                headers: { 'Content-Type': 'application/json' },\n" +
"                body: JSON.stringify({ milestoneId: milestoneId, score: score })\n" +
"            });\n" +
"            currentRoadmap = await res.json();\n" +
"            renderRoadmap();\n" +
"        }\n" +
"\n" +
"        async function renderAnalytics() {\n" +
"            const res = await fetch('/api/analytics');\n" +
"            const mastery = await res.json();\n" +
"\n" +
"            const labels = Object.keys(mastery);\n" +
"            const data = Object.values(mastery);\n" +
"\n" +
"            // Render Category Bars\n" +
"            const barsContainer = document.getElementById('category-bars');\n" +
"            barsContainer.innerHTML = '';\n" +
"            labels.forEach((cat, idx) => {\n" +
"                const val = data[idx];\n" +
"                barsContainer.innerHTML += `\n" +
"                    <div>\n" +
"                        <div class=\"flex justify-between text-xs font-semibold text-slate-700 mb-1\">\n" +
"                            <span>${cat}</span>\n" +
"                            <span>${val}%</span>\n" +
"                        </div>\n" +
"                        <div class=\"w-full bg-slate-100 rounded-full h-2.5\">\n" +
"                            <div class=\"bg-indigo-600 h-2.5 rounded-full\" style=\"width: ${val}%\"></div>\n" +
"                        </div>\n" +
"                    </div>\n" +
"                `;\n" +
"            });\n" +
"\n" +
"            // Render Radar Chart\n" +
"            const ctx = document.getElementById('radarChartCanvas').getContext('2d');\n" +
"            if (radarChart) radarChart.destroy();\n" +
"\n" +
"            radarChart = new Chart(ctx, {\n" +
"                type: 'radar',\n" +
"                data: {\n" +
"                    labels: labels,\n" +
"                    datasets: [{\n" +
"                        label: 'Mastery Level (%)',\n" +
"                        data: data,\n" +
"                        backgroundColor: 'rgba(79, 70, 229, 0.2)',\n" +
"                        borderColor: 'rgba(79, 70, 229, 1)',\n" +
"                        pointBackgroundColor: 'rgba(79, 70, 229, 1)',\n" +
"                        pointBorderColor: '#fff',\n" +
"                        borderWidth: 2\n" +
"                    }]\n" +
"                },\n" +
"                options: {\n" +
"                    scales: {\n" +
"                        r: {\n" +
"                            angleLines: { color: 'rgba(0, 0, 0, 0.1)' },\n" +
"                            suggestedMin: 0,\n" +
"                            suggestedMax: 100\n" +
"                        }\n" +
"                    }\n" +
"                }\n" +
"            });\n" +
"        }\n" +
"\n" +
"        function downloadMarkdown() {\n" +
"            window.location.href = '/api/export';\n" +
"        }\n" +
"\n" +
"        // Auto-initialize on load\n" +
"        window.onload = function() {\n" +
"            generateRoadmap();\n" +
"        };\n" +
"    </script>\n" +
"</body>\n" +
"</html>";
    }
}
