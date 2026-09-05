import os
import unittest
from core.knowledge_graph import KnowledgeGraph
from core.recommender import ContentBasedRecommender
from core.adaptive_engine import AdaptiveEngine

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA_DIR = os.path.join(BASE_DIR, "data")
SKILL_GRAPH_PATH = os.path.join(DATA_DIR, "skill_graph.json")
COURSES_PATH = os.path.join(DATA_DIR, "courses_dataset.json")
QUIZZES_PATH = os.path.join(DATA_DIR, "diagnostic_quizzes.json")


class TestLearningPathSystem(unittest.TestCase):

    def setUp(self):
        self.kg = KnowledgeGraph(SKILL_GRAPH_PATH)
        self.recommender = ContentBasedRecommender(COURSES_PATH)
        self.adaptive = AdaptiveEngine(self.kg, self.recommender)

    def test_knowledge_graph_loading(self):
        self.assertGreater(len(self.kg.domains), 0)
        self.assertGreater(len(self.kg.nodes), 0)
        self.assertIn("Artificial Intelligence & Machine Learning", self.kg.domains)

    def test_topological_sort_prerequisites(self):
        """Topological sort must ensure prerequisites appear strictly before dependent topics."""
        domain = "Artificial Intelligence & Machine Learning"
        sorted_topics = self.kg.get_topological_sort(domain)
        topic_indices = {t["id"]: idx for idx, t in enumerate(sorted_topics)}

        for topic in sorted_topics:
            tid = topic["id"]
            for prereq in topic.get("prerequisites", []):
                if prereq in topic_indices:
                    self.assertLess(
                        topic_indices[prereq],
                        topic_indices[tid],
                        f"Prerequisite {prereq} must precede {tid}"
                    )

    def test_recommender_scoring(self):
        user_profile = {
            "name": "Jane Doe",
            "domain": "Artificial Intelligence & Machine Learning",
            "experience_level": "Beginner",
            "hours_per_week": 10,
            "preferred_media": "Video Course",
            "interests": "Python syntax, programming fundamentals"
        }
        recs = self.recommender.recommend_for_topic("py_basics", user_profile, top_k=2)
        self.assertGreaterEqual(len(recs), 1)
        self.assertIn("match_score", recs[0])
        # Top recommended should prefer Video Course due to media bonus
        self.assertEqual(recs[0]["media_type"], "Video Course")

    def test_diagnostic_evaluation(self):
        domain = "Artificial Intelligence & Machine Learning"
        # Mock answers where q1 is correct (2), others incorrect (0)
        mock_answers = {
            "aiml_q1": 2,
            "aiml_q2": 0,
            "aiml_q3": 0,
            "aiml_q4": 0,
            "aiml_q5": 0
        }
        quiz_questions = [
            {"id": "aiml_q1", "topic_id": "py_basics", "correct_idx": 2, "difficulty": "Beginner", "explanation": "test"},
            {"id": "aiml_q2", "topic_id": "math_la", "correct_idx": 1, "difficulty": "Beginner", "explanation": "test"}
        ]
        result = self.adaptive.evaluate_diagnostic(domain, mock_answers, quiz_questions)
        self.assertEqual(result["score"], 1)
        self.assertEqual(result["total"], 2)
        self.assertEqual(result["percentage"], 50.0)

    def test_adaptive_remediation(self):
        """If quiz score is below 60%, remedial module must be injected."""
        user_profile = {
            "name": "Bob",
            "domain": "Full-Stack Web Development",
            "experience_level": "Beginner",
            "hours_per_week": 8,
            "preferred_media": "Interactive / Hands-on",
            "interests": "React and CSS"
        }
        roadmap = self.adaptive.generate_roadmap(user_profile)
        self.assertGreater(len(roadmap["milestones"]), 0)

        first_milestone = roadmap["milestones"][0]
        m_id = first_milestone["milestone_id"]
        initial_hours = first_milestone["estimated_hours"]

        # Simulate low score (45%)
        adapted_roadmap = self.adaptive.adapt_milestone(roadmap, m_id, 45.0)
        adapted_m = adapted_roadmap["milestones"][0]

        self.assertEqual(adapted_m["status"], "Needs Remediation")
        self.assertTrue(adapted_m["remedial_added"])
        self.assertEqual(adapted_m["estimated_hours"], initial_hours + 4)
        self.assertTrue(any("Remediation" in r.get("title", "") for r in adapted_m["recommended_resources"]))


if __name__ == "__main__":
    unittest.main()
