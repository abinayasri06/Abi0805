package com.learningpath.data;

import com.learningpath.model.Course;
import com.learningpath.model.Question;
import com.learningpath.model.Topic;

import java.util.*;

public class DataLoader {

    public static List<String> getDomains() {
        return Arrays.asList(
            "Artificial Intelligence & Machine Learning",
            "Full-Stack Web Development",
            "Data Science & Analytics",
            "Cybersecurity Essentials",
            "Cloud Computing & DevOps"
        );
    }

    public static List<Topic> getAllTopics() {
        List<Topic> topics = new ArrayList<>();

        // 1. AI & Machine Learning Track
        topics.add(new Topic("py_basics", "Python Programming Fundamentals",
            "Artificial Intelligence & Machine Learning", "Foundations", "Beginner", 12,
            Collections.emptyList(), "Variables, loops, functions, OOP, and error handling in Python."));
        topics.add(new Topic("math_la", "Linear Algebra & Vector Calculus",
            "Artificial Intelligence & Machine Learning", "Mathematics", "Beginner", 14,
            Collections.emptyList(), "Vectors, matrices, dot products, eigenvalues, gradients, and partial derivatives."));
        topics.add(new Topic("data_manipulation", "Data Wrangling with NumPy & Pandas",
            "Artificial Intelligence & Machine Learning", "Data Engineering", "Intermediate", 15,
            Collections.singletonList("py_basics"), "Multidimensional arrays, dataframes, indexing, cleaning, and aggregation."));
        topics.add(new Topic("prob_stats", "Probability & Applied Statistics",
            "Artificial Intelligence & Machine Learning", "Mathematics", "Intermediate", 12,
            Collections.singletonList("math_la"), "Distributions, Bayes theorem, hypothesis testing, p-values, and confidence intervals."));
        topics.add(new Topic("classical_ml", "Supervised & Unsupervised Machine Learning",
            "Artificial Intelligence & Machine Learning", "Core AI", "Intermediate", 20,
            Arrays.asList("data_manipulation", "prob_stats"), "Linear regression, decision trees, random forests, SVM, and k-means clustering."));
        topics.add(new Topic("feature_eng", "Feature Engineering & Model Evaluation",
            "Artificial Intelligence & Machine Learning", "Core AI", "Intermediate", 10,
            Collections.singletonList("classical_ml"), "Feature scaling, one-hot encoding, PCA, ROC-AUC, and bias-variance tradeoff."));
        topics.add(new Topic("deep_learning", "Deep Learning & Neural Networks",
            "Artificial Intelligence & Machine Learning", "Advanced AI", "Advanced", 25,
            Arrays.asList("classical_ml", "feature_eng"), "Perceptrons, backpropagation, loss functions, PyTorch tensors, and training loops."));
        topics.add(new Topic("nlp_transformers", "NLP & Large Language Models (Transformers)",
            "Artificial Intelligence & Machine Learning", "Advanced AI", "Advanced", 22,
            Collections.singletonList("deep_learning"), "Tokenization, self-attention, BERT, GPT architectures, and fine-tuning."));
        topics.add(new Topic("computer_vision", "Computer Vision & CNN Architectures",
            "Artificial Intelligence & Machine Learning", "Advanced AI", "Advanced", 20,
            Collections.singletonList("deep_learning"), "Convolutions, pooling, YOLO object detection, and image segmentation."));
        topics.add(new Topic("mlops_deploy", "MLOps & Model Deployment",
            "Artificial Intelligence & Machine Learning", "Production", "Advanced", 16,
            Collections.singletonList("classical_ml"), "Model serving with FastAPI, Docker containers, and drift monitoring."));

        // 2. Full-Stack Web Development Track
        topics.add(new Topic("html_css", "HTML5 & Modern CSS3 Flexbox/Grid",
            "Full-Stack Web Development", "Frontend", "Beginner", 10,
            Collections.emptyList(), "Semantic HTML, responsive viewport design, Flexbox, and CSS Grid layouts."));
        topics.add(new Topic("js_foundations", "JavaScript ES6+ & DOM Events",
            "Full-Stack Web Development", "Frontend", "Beginner", 16,
            Collections.singletonList("html_css"), "Arrow functions, promises, async/await, closures, and browser DOM manipulation."));
        topics.add(new Topic("react_basics", "React.js & State Management",
            "Full-Stack Web Development", "Frontend", "Intermediate", 20,
            Collections.singletonList("js_foundations"), "Components, JSX, useState, useEffect, custom hooks, and Context API."));
        topics.add(new Topic("node_express", "Node.js & Express RESTful APIs",
            "Full-Stack Web Development", "Backend", "Intermediate", 18,
            Collections.singletonList("js_foundations"), "Event loop, Express middleware, routing, JWT authentication, and JSON APIs."));
        topics.add(new Topic("databases_sql_nosql", "Databases (PostgreSQL & MongoDB)",
            "Full-Stack Web Development", "Backend", "Intermediate", 15,
            Collections.singletonList("node_express"), "Relational schemas, SQL queries, indexing, NoSQL document collections."));
        topics.add(new Topic("fullstack_integration", "Full-Stack System Architecture & Auth",
            "Full-Stack Web Development", "Fullstack", "Advanced", 20,
            Arrays.asList("react_basics", "databases_sql_nosql"), "Client-server communication, cookies, OAuth2, and end-to-end testing."));

        // 3. Data Science & Analytics Track
        topics.add(new Topic("sql_analysis", "SQL for Data Science & Business Intelligence",
            "Data Science & Analytics", "Data Analysis", "Beginner", 12,
            Collections.emptyList(), "Aggregations, Window functions (ROW_NUMBER), JOINs, and CTE subqueries."));
        topics.add(new Topic("python_ds", "Python for Data Science",
            "Data Science & Analytics", "Data Analysis", "Beginner", 14,
            Collections.emptyList(), "Pandas series, DataFrame manipulation, date/time handling, and cleaning."));
        topics.add(new Topic("eda_visualization", "Exploratory Data Analysis & Visual Storytelling",
            "Data Science & Analytics", "Visualization", "Intermediate", 14,
            Arrays.asList("python_ds", "sql_analysis"), "Seaborn, Matplotlib, boxplots, correlation heatmaps, and dashboard design."));
        topics.add(new Topic("statistical_modeling", "Statistical Modeling & A/B Testing",
            "Data Science & Analytics", "Analytics", "Intermediate", 16,
            Collections.singletonList("eda_visualization"), "Hypothesis testing, p-values, ANOVA, and randomized controlled experiment design."));
        topics.add(new Topic("predictive_analytics", "Predictive Analytics & Time Series Forecasting",
            "Data Science & Analytics", "Advanced Analytics", "Advanced", 18,
            Collections.singletonList("statistical_modeling"), "Time-series decomposition, ARIMA, Prophet forecasting, and churn prediction."));

        // 4. Cybersecurity Essentials Track
        topics.add(new Topic("net_fundamentals", "Computer Networking & Protocols",
            "Cybersecurity Essentials", "Foundations", "Beginner", 14,
            Collections.emptyList(), "OSI model, TCP/IP, DNS, DHCP, subnetting, and Wireshark packet captures."));
        topics.add(new Topic("linux_security", "Linux OS & System Hardening",
            "Cybersecurity Essentials", "Foundations", "Beginner", 12,
            Collections.emptyList(), "Bash shell scripting, file permissions (chmod/chown), SSH keys, and system logs."));
        topics.add(new Topic("security_defenses", "Network Security & Applied Cryptography",
            "Cybersecurity Essentials", "Defense", "Intermediate", 18,
            Arrays.asList("net_fundamentals", "linux_security"), "Symmetric/Asymmetric encryption, RSA, AES, TLS handshakes, and firewalls."));
        topics.add(new Topic("web_penetration", "Ethical Hacking & Web Vulnerabilities",
            "Cybersecurity Essentials", "Offensive", "Intermediate", 20,
            Collections.singletonList("security_defenses"), "OWASP Top 10, SQL Injection, XSS, CSRF, Burp Suite, and remediation."));
        topics.add(new Topic("soc_incident_response", "SOC Analysis & Incident Response",
            "Cybersecurity Essentials", "Operations", "Advanced", 18,
            Collections.singletonList("web_penetration"), "SIEM log analysis (Splunk/Elastic), malware triage, and defense playbooks."));

        // 5. Cloud Computing & DevOps Track
        topics.add(new Topic("cloud_fundamentals", "Cloud Fundamentals (AWS/Azure)",
            "Cloud Computing & DevOps", "Cloud Core", "Beginner", 12,
            Collections.emptyList(), "IaaS vs PaaS vs SaaS, regions, IAM roles, EC2 instances, and S3 storage."));
        topics.add(new Topic("docker_containers", "Docker & Container Architecture",
            "Cloud Computing & DevOps", "DevOps", "Intermediate", 14,
            Collections.singletonList("cloud_fundamentals"), "Dockerfiles, layers, multi-stage builds, networking, and Docker Compose."));
        topics.add(new Topic("kubernetes_orchestration", "Kubernetes Cluster Orchestration",
            "Cloud Computing & DevOps", "DevOps", "Advanced", 22,
            Collections.singletonList("docker_containers"), "Pods, Deployments, Services, ConfigMaps, Ingress, and Helm charts."));
        topics.add(new Topic("iac_terraform", "Infrastructure as Code with Terraform",
            "Cloud Computing & DevOps", "Infrastructure", "Advanced", 16,
            Collections.singletonList("cloud_fundamentals"), "HCL declarative code, state files, providers, and automated cloud provisioning."));
        topics.add(new Topic("cicd_pipelines", "CI/CD Automation with GitHub Actions",
            "Cloud Computing & DevOps", "Automation", "Advanced", 14,
            Collections.singletonList("docker_containers"), "Automated testing workflows, build triggers, secret management, and deployments."));

        return topics;
    }

    public static List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();

        courses.add(new Course("c_py_01", "py_basics", "Python for Everybody: Complete Bootcamp",
            "FreeCodeCamp", "Video Course", "Beginner", 12, 4.8,
            "Master core Python syntax, loops, functions, file I/O, and object-oriented programming.",
            "https://www.freecodecamp.org/news/python-for-everybody/", Arrays.asList("python", "programming", "video", "basics")));
        courses.add(new Course("c_py_02", "py_basics", "Interactive Python Exercises & Logic Drills",
            "Exercism", "Interactive / Hands-on", "Beginner", 8, 4.9,
            "Hands-on coding challenges to test algorithms, list comprehensions, and OOP patterns in Python.",
            "https://exercism.org/tracks/python", Arrays.asList("python", "interactive", "hands-on", "exercises")));

        courses.add(new Course("c_la_01", "math_la", "Essence of Linear Algebra Visual Series",
            "3Blue1Brown", "Video Course", "Beginner", 8, 5.0,
            "Visual intuition for vectors, linear transformations, matrix multiplication, determinant, and eigenvectors.",
            "https://www.youtube.com/playlist?list=PLZHQObOWTQDPD3MizzM2xVFitgF8hE_ab", Arrays.asList("linear-algebra", "math", "video", "visual")));
        courses.add(new Course("c_la_02", "math_la", "Computational Linear Algebra with Python",
            "Fast.ai", "Documentation & Articles", "Intermediate", 10, 4.7,
            "Matrix factorizations, Singular Value Decomposition (SVD), and numerical stability.",
            "https://github.com/fastai/numerical-linear-algebra", Arrays.asList("linear-algebra", "math", "reading", "theory")));

        courses.add(new Course("c_np_01", "data_manipulation", "NumPy & Pandas for Fast Data Wrangling",
            "Kaggle Learn", "Interactive / Hands-on", "Intermediate", 8, 4.8,
            "Clean dirty data, index complex tables, handle missing values, and transform dataframes.",
            "https://www.kaggle.com/learn/pandas", Arrays.asList("pandas", "numpy", "data", "wrangling", "interactive")));
        courses.add(new Course("c_np_02", "data_manipulation", "Python Data Analysis: Zero to Pandas",
            "Jovian / YouTube", "Video Course", "Intermediate", 12, 4.7,
            "Hands-on video course on tabular data analysis with real-world COVID-19 and financial datasets.",
            "https://jovian.com/", Arrays.asList("pandas", "numpy", "video", "data")));

        courses.add(new Course("c_stats_01", "prob_stats", "StatQuest: Applied Probability & Statistics",
            "StatQuest with Josh Starmer", "Video Course", "Intermediate", 10, 4.9,
            "Clear visual breakdowns of distributions, p-values, hypothesis tests, and confidence intervals.",
            "https://statquest.org/", Arrays.asList("statistics", "probability", "video", "hypothesis-testing")));

        courses.add(new Course("c_ml_01", "classical_ml", "Machine Learning Specialization",
            "DeepLearning.AI / Andrew Ng", "Video Course", "Intermediate", 20, 4.9,
            "Regression, decision trees, random forests, SVM, and k-means clustering explained from first principles.",
            "https://www.coursera.org/specializations/machine-learning-introduction", Arrays.asList("machine-learning", "algorithms", "video", "core-ai")));
        courses.add(new Course("c_ml_02", "classical_ml", "Hands-On Scikit-Learn Machine Learning Projects",
            "GitHub / Community", "Guided Project", "Intermediate", 15, 4.8,
            "Build practical end-to-end models: House price prediction, customer churn analysis, and spam detection.",
            "https://github.com/ageron/handson-ml3", Arrays.asList("scikit-learn", "projects", "practical", "guided-project")));

        courses.add(new Course("c_fe_01", "feature_eng", "Feature Engineering & Preprocessing Masterclass",
            "Kaggle Learn", "Interactive / Hands-on", "Intermediate", 8, 4.7,
            "Mutual information, target encoding, principal component analysis (PCA), and scaling transformations.",
            "https://www.kaggle.com/learn/feature-engineering", Arrays.asList("feature-engineering", "pca", "interactive")));

        courses.add(new Course("c_dl_01", "deep_learning", "Deep Learning with PyTorch: Zero to Mastery",
            "PyTorch Official Tutorials", "Documentation & Articles", "Advanced", 16, 4.9,
            "Tensors, autograd, building deep neural networks, loss functions, and backpropagation mechanics.",
            "https://pytorch.org/tutorials/", Arrays.asList("pytorch", "deep-learning", "neural-networks", "documentation")));
        courses.add(new Course("c_dl_02", "deep_learning", "Practical Deep Learning for Coders",
            "Fast.ai", "Video Course", "Advanced", 20, 4.9,
            "Top-down approach to training modern neural networks, transfer learning, and computer vision models.",
            "https://course.fast.ai/", Arrays.asList("deep-learning", "fastai", "pytorch", "video", "practical")));

        courses.add(new Course("c_nlp_01", "nlp_transformers", "Hugging Face NLP & Transformers Course",
            "Hugging Face", "Interactive / Hands-on", "Advanced", 18, 4.9,
            "Tokenizers, fine-tuning transformer models, sentiment analysis, and building LLM applications.",
            "https://huggingface.co/learn/nlp-course/", Arrays.asList("transformers", "nlp", "llm", "huggingface", "interactive")));

        courses.add(new Course("c_cv_01", "computer_vision", "Computer Vision & YOLO Object Detection",
            "OpenCV / Ultralytics", "Guided Project", "Advanced", 15, 4.8,
            "Real-time object detection with YOLOv8, bounding boxes, video streams, and OpenCV filters.",
            "https://docs.ultralytics.com/", Arrays.asList("computer-vision", "yolo", "opencv", "cnn", "project")));

        courses.add(new Course("c_mlops_01", "mlops_deploy", "Production MLOps: Packaging & Deployment",
            "Made With ML", "Documentation & Articles", "Advanced", 16, 4.9,
            "Packaging models with Docker, serving APIs with FastAPI, CI/CD testing, and MLflow tracking.",
            "https://madewithml.com/", Arrays.asList("mlops", "docker", "fastapi", "deployment", "production")));

        // Web Development Courses
        courses.add(new Course("c_web_01", "html_css", "Responsive Web Design Certification",
            "FreeCodeCamp", "Interactive / Hands-on", "Beginner", 12, 4.8,
            "Learn modern semantic HTML5 and responsive CSS3 including Flexbox and CSS Grid.",
            "https://www.freecodecamp.org/", Arrays.asList("html", "css", "web", "interactive", "responsive")));
        courses.add(new Course("c_web_02", "js_foundations", "JavaScript: Modern ES6+ In Depth",
            "JavaScript.info", "Documentation & Articles", "Beginner", 15, 4.9,
            "Event loop, closures, prototypes, asynchronous JavaScript, fetch API, and promises.",
            "https://javascript.info/", Arrays.asList("javascript", "es6", "async", "documentation", "frontend")));
        courses.add(new Course("c_web_03", "react_basics", "React Modern State & Hooks Bootcamp",
            "React.dev Official", "Interactive / Hands-on", "Intermediate", 18, 4.8,
            "Component-driven development, custom hooks, context management, and performance optimization.",
            "https://react.dev/learn", Arrays.asList("react", "frontend", "hooks", "components", "interactive")));
        courses.add(new Course("c_web_04", "node_express", "Node.js & Express RESTful API Development",
            "FreeCodeCamp / Traversy", "Video Course", "Intermediate", 16, 4.7,
            "Build scalable REST APIs, configure middleware, handle authentication with JWT, and error handling.",
            "https://www.freecodecamp.org/", Arrays.asList("node", "express", "backend", "api", "video")));
        courses.add(new Course("c_web_05", "databases_sql_nosql", "Full Database Mastery: PostgreSQL & MongoDB",
            "Prisma Data Guide", "Interactive / Hands-on", "Intermediate", 14, 4.8,
            "Relational schema design, normalization, ACID transactions, and MongoDB aggregation pipelines.",
            "https://www.prisma.io/dataguide", Arrays.asList("database", "sql", "postgresql", "mongodb", "backend")));
        courses.add(new Course("c_web_06", "fullstack_integration", "FullStack Open: Deep Dive into Modern Web",
            "Univ of Helsinki", "Guided Project", "Advanced", 20, 5.0,
            "Integrate React frontend with Express/Postgres backend, OAuth2, and automated test suites.",
            "https://fullstackopen.com/", Arrays.asList("fullstack", "react", "node", "guided-project", "testing")));

        // Data Science Courses
        courses.add(new Course("c_ds_01", "sql_analysis", "Advanced SQL for Data Analysis & BI",
            "Mode Analytics", "Interactive / Hands-on", "Beginner", 12, 4.9,
            "Complex queries with joins, window functions (ROW_NUMBER, LEAD/LAG), and subqueries.",
            "https://mode.com/sql-tutorial/", Arrays.asList("sql", "analytics", "business-intelligence", "interactive")));
        courses.add(new Course("c_ds_02", "python_ds", "Python Data Science Handbook",
            "O'Reilly / Jake VanderPlas", "Documentation & Articles", "Beginner", 15, 4.9,
            "The essential guide to computing with Python, NumPy, Pandas, and Matplotlib.",
            "https://jakevdp.github.io/PythonDataScienceHandbook/", Arrays.asList("python", "data-science", "handbook", "documentation")));
        courses.add(new Course("c_ds_03", "eda_visualization", "Visual Storytelling with Seaborn & Tableau",
            "Coursera / Tableau", "Video Course", "Intermediate", 12, 4.7,
            "Creating dashboards, choosing effective chart types, visual hierarchy, and data communication.",
            "https://www.coursera.org/", Arrays.asList("visualization", "tableau", "charts", "video")));
        courses.add(new Course("c_ds_04", "statistical_modeling", "A/B Testing & Causal Inference",
            "Google / Udacity", "Guided Project", "Intermediate", 14, 4.8,
            "Hypothesis testing, metric selection, variance reduction, and interpreting split experiment results.",
            "https://www.udacity.com/", Arrays.asList("ab-testing", "statistics", "experiments", "guided-project")));
        courses.add(new Course("c_ds_05", "predictive_analytics", "Time Series Forecasting with Prophet & ARIMA",
            "Meta Prophet Docs", "Interactive / Hands-on", "Advanced", 16, 4.8,
            "Decomposition, trend and seasonality modeling, revenue prediction, and model evaluation.",
            "https://facebook.github.io/prophet/", Arrays.asList("time-series", "forecasting", "predictive", "interactive")));

        // Cybersecurity Courses
        courses.add(new Course("c_sec_01", "net_fundamentals", "Computer Networking Full Course for Beginners",
            "Professor Messer", "Video Course", "Beginner", 14, 4.9,
            "Detailed packet flows, routing protocols, ports, subnets, and Wireshark packet captures.",
            "https://www.youtube.com/", Arrays.asList("networking", "tcp-ip", "wireshark", "video")));
        courses.add(new Course("c_sec_02", "linux_security", "Linux Command Line & Hardening Guide",
            "OverTheWire Bandit", "Interactive / Hands-on", "Beginner", 12, 4.9,
            "Hands-on wargame exercises learning Linux shell, SSH security, and file permissions.",
            "https://overthewire.org/wargames/bandit/", Arrays.asList("linux", "security", "wargames", "interactive")));
        courses.add(new Course("c_sec_03", "security_defenses", "Applied Cryptography & Defense",
            "CryptoHack", "Interactive / Hands-on", "Intermediate", 16, 4.8,
            "RSA, Diffie-Hellman, AES block ciphers, TLS handshakes, and public key infrastructure.",
            "https://cryptohack.org/", Arrays.asList("cryptography", "ciphers", "defense", "interactive")));
        courses.add(new Course("c_sec_04", "web_penetration", "Web Security Academy (OWASP Top 10)",
            "PortSwigger", "Interactive / Hands-on", "Intermediate", 20, 5.0,
            "Exploit and remediate SQL injection, cross-site scripting (XSS), CSRF, and SSRF vulnerabilities.",
            "https://portswigger.net/web-security", Arrays.asList("ethical-hacking", "owasp", "penetration-testing", "interactive")));
        courses.add(new Course("c_sec_05", "soc_incident_response", "SOC Analyst & Incident Response Labs",
            "TryHackMe", "Guided Project", "Advanced", 18, 4.9,
            "Log analysis in Splunk, malware triage, memory analysis with Volatility, and defensive playbooks.",
            "https://tryhackme.com/", Arrays.asList("soc", "splunk", "incident-response", "guided-project")));

        // Cloud Computing Courses
        courses.add(new Course("c_cld_01", "cloud_fundamentals", "AWS Certified Cloud Practitioner Training",
            "AWS Skill Builder", "Video Course", "Beginner", 12, 4.8,
            "Cloud computing models, AWS global infrastructure, core services (EC2, S3, RDS), and IAM.",
            "https://aws.amazon.com/", Arrays.asList("aws", "cloud", "infrastructure", "video")));
        courses.add(new Course("c_cld_02", "docker_containers", "Docker Mastery with Swarm and Kubernetes",
            "Docker Docs", "Interactive / Hands-on", "Intermediate", 14, 4.9,
            "Dockerfile best practices, volume mounting, networking, and multi-service docker compose setups.",
            "https://docs.docker.com/", Arrays.asList("docker", "containers", "devops", "interactive")));
        courses.add(new Course("c_cld_03", "kubernetes_orchestration", "Kubernetes The Hard Way & Hands-on Labs",
            "KodeKloud", "Guided Project", "Advanced", 20, 5.0,
            "Bootstrap a cluster from scratch, master deployments, services, ingress, and rolling updates.",
            "https://github.com/kelseyhightower/kubernetes-the-hard-way", Arrays.asList("kubernetes", "k8s", "orchestration", "guided-project")));
        courses.add(new Course("c_cld_04", "iac_terraform", "Automating Multi-Cloud with Terraform",
            "HashiCorp Learn", "Documentation & Articles", "Advanced", 15, 4.8,
            "Declarative cloud provisioning, remote state locking in S3, and zero-downtime rollouts.",
            "https://developer.hashicorp.com/terraform", Arrays.asList("terraform", "iac", "cloud", "documentation")));
        courses.add(new Course("c_cld_05", "cicd_pipelines", "Production CI/CD Pipelines with GitHub Actions",
            "GitHub Skills", "Interactive / Hands-on", "Advanced", 12, 4.8,
            "Automate unit tests, build container images, run security vulnerability scans, and deploy.",
            "https://skills.github.com/", Arrays.asList("github-actions", "cicd", "automation", "devops", "interactive")));

        return courses;
    }

    public static List<Question> getDiagnosticQuestions(String domain) {
        List<Question> questions = new ArrayList<>();

        if ("Artificial Intelligence & Machine Learning".equals(domain)) {
            questions.add(new Question("aiml_q1", domain, "py_basics", "Beginner",
                "Which Python data structure is mutable, ordered, and allows duplicate elements?",
                Arrays.asList("Tuple", "Set", "List", "Dictionary key"), 2,
                "Lists in Python are mutable and ordered sequences that permit duplicate elements."));
            questions.add(new Question("aiml_q2", domain, "math_la", "Beginner",
                "If two non-zero vectors have a dot product equal to 0, what does this signify?",
                Arrays.asList("The vectors are parallel", "The vectors are orthogonal (perpendicular)", "The vectors have identical magnitude", "One vector is inverse of the other"), 1,
                "A dot product of zero indicates an angle of 90 degrees (cos(90) = 0), meaning they are orthogonal."));
            questions.add(new Question("aiml_q3", domain, "data_manipulation", "Intermediate",
                "In Pandas, which method is used to fill missing values with the column mean?",
                Arrays.asList("df.dropna()", "df.fillna(df.mean())", "df.replace_null(method='mean')", "df.impute()"), 1,
                "df.fillna(df.mean()) imputes missing/NaN values with column averages."));
            questions.add(new Question("aiml_q4", domain, "classical_ml", "Intermediate",
                "What is the primary indicator of High Variance in a Machine Learning model?",
                Arrays.asList("Underfitting: fails on training data", "Overfitting: performs well on training data but poorly on unseen test data", "Weights collapse to zero", "Training time decreases"), 1,
                "High variance signifies overfitting to training set noise and poor generalization."));
            questions.add(new Question("aiml_q5", domain, "deep_learning", "Advanced",
                "Why is the ReLU activation function commonly preferred over Sigmoid in deep hidden layers?",
                Arrays.asList("It scales outputs strictly between 0 and 1", "It mitigates the vanishing gradient problem and computes faster", "It is differentiable everywhere including 0", "It is bounded above"), 1,
                "ReLU avoids gradient saturation for positive activations, mitigating vanishing gradients during backpropagation."));
        } else if ("Full-Stack Web Development".equals(domain)) {
            questions.add(new Question("web_q1", domain, "html_css", "Beginner",
                "Which CSS property provides flexible one-dimensional row or column layout?",
                Arrays.asList("display: block", "display: flex", "display: table", "display: inline"), 1,
                "Flexbox (display: flex) is a CSS layout mode specifically designed for 1-dimensional layouts."));
            questions.add(new Question("web_q2", domain, "js_foundations", "Beginner",
                "What does 'typeof NaN' evaluate to in JavaScript?",
                Arrays.asList("'undefined'", "'null'", "'number'", "'NaN'"), 2,
                "In JavaScript specification, NaN is technically of the 'number' type."));
            questions.add(new Question("web_q3", domain, "react_basics", "Intermediate",
                "In React, which hook is used to execute side effects like data fetching or subscriptions?",
                Arrays.asList("useState", "useReducer", "useEffect", "useMemo"), 2,
                "useEffect lets components synchronize with external APIs and handle lifecycle side effects."));
            questions.add(new Question("web_q4", domain, "node_express", "Intermediate",
                "What is the role of Express middleware in Node.js?",
                Arrays.asList("Compile JSX to HTML", "Intercept and modify req/res objects and invoke next()", "Manage database transactions directly", "Act as a web browser engine"), 1,
                "Middleware functions intercept incoming HTTP requests and outgoing responses before routes execute."));
            questions.add(new Question("web_q5", domain, "databases_sql_nosql", "Advanced",
                "Which HTTP header is standard for passing JSON Web Tokens (JWT)?",
                Arrays.asList("Content-Type: application/jwt", "Authorization: Bearer <token>", "Set-Cookie: jwt=<token>", "X-Requested-With: JWT"), 1,
                "Standard REST APIs pass authentication tokens via the Authorization header using the Bearer schema."));
        } else if ("Data Science & Analytics".equals(domain)) {
            questions.add(new Question("ds_q1", domain, "sql_analysis", "Beginner",
                "Which SQL clause filters aggregated groups produced by GROUP BY?",
                Arrays.asList("WHERE", "ORDER BY", "HAVING", "LIMIT"), 2,
                "HAVING filters groups post-aggregation; WHERE filters individual rows pre-aggregation."));
            questions.add(new Question("ds_q2", domain, "python_ds", "Beginner",
                "In Pandas, what does df.describe() compute?",
                Arrays.asList("Summary of column data types", "Descriptive statistics (mean, std, min, percentiles, max)", "First 5 rows", "SQL table schema"), 1,
                "df.describe() calculates summary statistics for numerical columns in a DataFrame."));
            questions.add(new Question("ds_q3", domain, "eda_visualization", "Intermediate",
                "Which chart type is best suited to display median, quartiles, and outliers?",
                Arrays.asList("Pie Chart", "Box Plot (Box-and-Whisker)", "Stacked Bar Chart", "Donut Chart"), 1,
                "Box plots show medians, interquartile ranges, and outliers clearly."));
            questions.add(new Question("ds_q4", domain, "statistical_modeling", "Intermediate",
                "In hypothesis testing, a p-value < 0.05 implies:",
                Arrays.asList("The null hypothesis is definitely true", "We reject the null hypothesis in favor of the alternative", "Sample size was insufficient", "Variance is zero"), 1,
                "p-value < alpha provides statistically significant evidence to reject the null hypothesis."));
            questions.add(new Question("ds_q5", domain, "predictive_analytics", "Advanced",
                "What does stationarity in a time series mean?",
                Arrays.asList("Autocorrelation is high", "Statistical properties (mean, variance) remain constant over time", "Exponential growth occurs", "Seasonality dominates"), 1,
                "A stationary time series has constant mean and variance across time."));
        } else if ("Cybersecurity Essentials".equals(domain)) {
            questions.add(new Question("sec_q1", domain, "net_fundamentals", "Beginner",
                "Which layer of the OSI model handles end-to-end communication and port numbers (TCP/UDP)?",
                Arrays.asList("Network Layer (L3)", "Transport Layer (L4)", "Data Link Layer (L2)", "Session Layer (L5)"), 1,
                "Layer 4 (Transport) is responsible for host-to-host communication, reliability, and port multiplexing."));
            questions.add(new Question("sec_q2", domain, "linux_security", "Beginner",
                "In Linux, what numeric octal permission gives Read, Write, Execute to owner and Read-only to others?",
                Arrays.asList("777", "644", "744", "755"), 2,
                "Owner: 4+2+1=7; Group: 4; Others: 4 -> 744."));
            questions.add(new Question("sec_q3", domain, "security_defenses", "Intermediate",
                "What is the primary difference between symmetric and asymmetric cryptography?",
                Arrays.asList("Symmetric uses two keys; asymmetric uses one", "Symmetric uses a shared single key; asymmetric uses a public/private key pair", "Symmetric cannot encrypt large files", "Asymmetric uses no math"), 1,
                "Symmetric encryption utilizes one shared secret key; asymmetric encryption utilizes key pairs."));
            questions.add(new Question("sec_q4", domain, "web_penetration", "Intermediate",
                "What is the most effective defense against SQL Injection?",
                Arrays.asList("Regex filtering of single quotes", "Parameterized Queries (Prepared Statements)", "Database encryption", "Shortening query text"), 1,
                "Parameterized queries treat user input strictly as data parameters rather than executable SQL code."));
            questions.add(new Question("sec_q5", domain, "soc_incident_response", "Advanced",
                "What does SIEM stand for in SOC operations?",
                Arrays.asList("Security Information and Event Management", "System Information and Encryption Module", "Secure IP Ethernet Mechanism", "Server Inspection Monitor"), 0,
                "SIEM stands for Security Information and Event Management."));
        } else {
            // Cloud Computing & DevOps
            questions.add(new Question("cld_q1", domain, "cloud_fundamentals", "Beginner",
                "Which cloud service model provides virtualized servers, storage, and networking with maximum OS control?",
                Arrays.asList("SaaS", "PaaS", "IaaS", "FaaS"), 2,
                "Infrastructure as a Service (IaaS) provides virtualized raw compute and storage."));
            questions.add(new Question("cld_q2", domain, "docker_containers", "Intermediate",
                "What is the primary function of a Dockerfile?",
                Arrays.asList("Configure AWS network switches", "A text script containing instructions to assemble a container image", "Run load tests", "Store production passwords"), 1,
                "A Dockerfile contains instructions that docker build executes to create container images."));
            questions.add(new Question("cld_q3", domain, "kubernetes_orchestration", "Advanced",
                "What is the smallest deployable execution unit in Kubernetes?",
                Arrays.asList("Cluster", "Pod", "Node", "Namespace"), 1,
                "A Pod encapsulates one or more co-located containers that share storage and network."));
            questions.add(new Question("cld_q4", domain, "iac_terraform", "Advanced",
                "In Terraform, what file stores current metadata of real-world deployed cloud infrastructure?",
                Arrays.asList("terraform.lock.hcl", "terraform.tfvars", "terraform.tfstate", "main.tf"), 2,
                "The terraform.tfstate file maps configuration definitions to actual provisioned cloud resources."));
            questions.add(new Question("cld_q5", domain, "cicd_pipelines", "Advanced",
                "What is the main goal of Continuous Integration (CI)?",
                Arrays.asList("Deploy code without testing", "Automatically merge, build, and test developer code changes into a shared repo", "Eliminate unit tests", "Manage hardware cables"), 1,
                "CI automates building and automated testing of incremental commits to discover regressions rapidly."));
        }

        return questions;
    }
}
