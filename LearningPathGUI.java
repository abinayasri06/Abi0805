package com.learningpath.gui;

import com.learningpath.core.AdaptiveEngine;
import com.learningpath.core.ContentRecommender;
import com.learningpath.core.KnowledgeGraph;
import com.learningpath.data.DataLoader;
import com.learningpath.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class LearningPathGUI extends JFrame {
    private KnowledgeGraph kg;
    private ContentRecommender recommender;
    private AdaptiveEngine adaptiveEngine;

    private UserProfile currentProfile;
    private DiagnosticResult currentDiagnostic;
    private Roadmap currentRoadmap;

    // UI Components
    private JTextField txtName;
    private JComboBox<String> cmbDomain;
    private JSlider sldHours;
    private JComboBox<String> cmbMedia;
    private JComboBox<String> cmbLevel;
    private JTextField txtInterests;

    private JTabbedPane tabbedPane;
    private JPanel diagQuestionsPanel;
    private JLabel lblDiagScore;
    private JLabel lblDiagLevel;

    private DefaultTableModel milestoneTableModel;
    private JTable milestoneTable;
    private JLabel lblTotalDuration;
    private JLabel lblTotalHours;
    private JLabel lblOverallProgress;

    public LearningPathGUI() {
        setTitle("AI Personalized Learning Path Recommendation System");
        setSize(1000, 700);
        setMinimumSize(new Dimension(850, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize Core Engines
        this.kg = new KnowledgeGraph(DataLoader.getAllTopics());
        this.recommender = new ContentRecommender(DataLoader.getAllCourses());
        this.adaptiveEngine = new AdaptiveEngine(this.kg, this.recommender);

        this.currentProfile = new UserProfile(
            "Alex Smith",
            "Artificial Intelligence & Machine Learning",
            "Beginner",
            10,
            "Video Course",
            "PyTorch, deep learning, NLP"
        );

        initUI();
    }

    private void initUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));

        tabbedPane.addTab("🎯 1. Profile & Goals", createProfilePanel());
        tabbedPane.addTab("📝 2. Diagnostic Assessment", createDiagnosticPanel());
        tabbedPane.addTab("🗺️ 3. Personalized Roadmap", createRoadmapPanel());
        tabbedPane.addTab("📊 4. Skill Analytics & DAG", createAnalyticsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Bottom status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        statusBar.setBackground(new Color(241, 245, 249));
        JLabel lblStatus = new JLabel("Engine: Java Knowledge Graph DAG (Kahn's Sort) + TF-IDF Vector Recommender");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(new Color(71, 85, 105));
        statusBar.add(lblStatus, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Step 1: Configure Learner Profile & Goals");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(new Color(30, 41, 59));
        panel.add(header, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Student Name / ID:"));
        txtName = new JTextField(currentProfile.getName());
        formPanel.add(txtName);

        formPanel.add(new JLabel("Target Specialization:"));
        cmbDomain = new JComboBox<>(DataLoader.getDomains().toArray(new String[0]));
        cmbDomain.setSelectedItem(currentProfile.getDomain());
        formPanel.add(cmbDomain);

        formPanel.add(new JLabel("Weekly Commitment (Hours/Week):"));
        sldHours = new JSlider(2, 30, currentProfile.getHoursPerWeek());
        sldHours.setPaintTicks(true);
        sldHours.setPaintLabels(true);
        sldHours.setMajorTickSpacing(7);
        sldHours.setBackground(Color.WHITE);
        formPanel.add(sldHours);

        formPanel.add(new JLabel("Preferred Primary Media Format:"));
        cmbMedia = new JComboBox<>(new String[]{"Video Course", "Interactive / Hands-on", "Documentation & Articles", "Guided Project"});
        cmbMedia.setSelectedItem(currentProfile.getPreferredMedia());
        formPanel.add(cmbMedia);

        formPanel.add(new JLabel("Self-Assessed Baseline Level:"));
        cmbLevel = new JComboBox<>(new String[]{"Beginner", "Intermediate", "Advanced"});
        cmbLevel.setSelectedItem(currentProfile.getExperienceLevel());
        formPanel.add(cmbLevel);

        formPanel.add(new JLabel("Key Focus Aspirations:"));
        txtInterests = new JTextField(currentProfile.getInterests());
        formPanel.add(txtInterests);

        panel.add(formPanel, BorderLayout.CENTER);

        JButton btnNext = new JButton("Save Profile & Proceed to Diagnostic Assessment ➡️");
        btnNext.setBackground(new Color(79, 70, 229));
        btnNext.setForeground(Color.WHITE);
        btnNext.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnNext.setFocusPainted(false);
        btnNext.addActionListener(e -> {
            currentProfile = new UserProfile(
                txtName.getText(),
                (String) cmbDomain.getSelectedItem(),
                (String) cmbLevel.getSelectedItem(),
                sldHours.getValue(),
                (String) cmbMedia.getSelectedItem(),
                txtInterests.getText()
            );
            loadDiagnosticQuestions();
            tabbedPane.setSelectedIndex(1);
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnNext);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDiagnosticPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Step 2: Diagnostic Skill Gap Assessment");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);

        diagQuestionsPanel = new JPanel();
        diagQuestionsPanel.setLayout(new BoxLayout(diagQuestionsPanel, BoxLayout.Y_AXIS));
        diagQuestionsPanel.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(diagQuestionsPanel);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        JPanel resultPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        resultPanel.setBackground(Color.WHITE);
        lblDiagScore = new JLabel("Score: -");
        lblDiagScore.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDiagScore.setForeground(new Color(79, 70, 229));

        lblDiagLevel = new JLabel("Level: -");
        lblDiagLevel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDiagLevel.setForeground(new Color(16, 185, 129));

        resultPanel.add(lblDiagScore);
        resultPanel.add(lblDiagLevel);
        bottomPanel.add(resultPanel, BorderLayout.WEST);

        JButton btnSubmit = new JButton("Submit Diagnostic & Generate Roadmap 🚀");
        btnSubmit.setBackground(new Color(16, 185, 129));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSubmit.addActionListener(e -> evaluateDiagnosticAndGenerate());

        bottomPanel.add(btnSubmit, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private Map<String, ButtonGroup> questionGroupMap = new HashMap<>();

    private void loadDiagnosticQuestions() {
        diagQuestionsPanel.removeAll();
        questionGroupMap.clear();

        List<Question> questions = DataLoader.getDiagnosticQuestions(currentProfile.getDomain());
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            JPanel qBox = new JPanel();
            qBox.setLayout(new BoxLayout(qBox, BoxLayout.Y_AXIS));
            qBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
                new EmptyBorder(10, 10, 10, 10)
            ));
            qBox.setBackground(Color.WHITE);

            JLabel qText = new JLabel("Q" + (i + 1) + ": " + q.getQuestionText() + " [" + q.getDifficulty() + "]");
            qText.setFont(new Font("Segoe UI", Font.BOLD, 13));
            qBox.add(qText);
            qBox.add(Box.createVerticalStrut(5));

            ButtonGroup bg = new ButtonGroup();
            for (int optIdx = 0; optIdx < q.getOptions().size(); optIdx++) {
                JRadioButton rb = new JRadioButton(q.getOptions().get(optIdx));
                rb.setBackground(Color.WHITE);
                rb.setActionCommand(String.valueOf(optIdx));
                if (optIdx == 0) rb.setSelected(true);
                bg.add(rb);
                qBox.add(rb);
            }
            questionGroupMap.put(q.getId(), bg);
            diagQuestionsPanel.add(qBox);
        }

        diagQuestionsPanel.revalidate();
        diagQuestionsPanel.repaint();
    }

    private void evaluateDiagnosticAndGenerate() {
        List<Question> questions = DataLoader.getDiagnosticQuestions(currentProfile.getDomain());
        Map<String, Integer> answers = new HashMap<>();

        for (Question q : questions) {
            ButtonGroup bg = questionGroupMap.get(q.getId());
            if (bg != null && bg.getSelection() != null) {
                answers.put(q.getId(), Integer.parseInt(bg.getSelection().getActionCommand()));
            }
        }

        currentDiagnostic = adaptiveEngine.evaluateDiagnostic(currentProfile.getDomain(), answers, questions);
        lblDiagScore.setText("Score: " + currentDiagnostic.getScore() + " / " + currentDiagnostic.getTotal() +
                             " (" + currentDiagnostic.getPercentage() + "%)");
        lblDiagLevel.setText("AI Verified Level: " + currentDiagnostic.getSuggestedLevel());

        currentRoadmap = adaptiveEngine.generateRoadmap(currentProfile, currentDiagnostic);
        updateRoadmapTable();
        tabbedPane.setSelectedIndex(2);
    }

    private JPanel createRoadmapPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        panel.setBackground(Color.WHITE);

        // Header Metrics
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        metricsPanel.setBackground(new Color(248, 250, 252));
        metricsPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        lblTotalDuration = new JLabel("Total Duration: - Weeks");
        lblTotalDuration.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalHours = new JLabel("Total Hours: - Hours");
        lblTotalHours.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOverallProgress = new JLabel("Progress: 0%");
        lblOverallProgress.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblOverallProgress.setForeground(new Color(16, 185, 129));

        metricsPanel.add(lblTotalDuration);
        metricsPanel.add(lblTotalHours);
        metricsPanel.add(lblOverallProgress);
        panel.add(metricsPanel, BorderLayout.NORTH);

        // Milestone Table
        String[] cols = {"Milestone", "Topic Name", "Category", "Difficulty", "Hours", "Weeks", "Status", "Score", "Top Recommended Resource"};
        milestoneTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        milestoneTable = new JTable(milestoneTableModel);
        milestoneTable.setRowHeight(28);
        milestoneTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        milestoneTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane tableScroll = new JScrollPane(milestoneTable);
        panel.add(tableScroll, BorderLayout.CENTER);

        // Action Panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(Color.WHITE);

        JButton btnTestAdapt = new JButton("🎯 Test Milestone Quiz & Trigger Dynamic Remediation");
        btnTestAdapt.setBackground(new Color(79, 70, 229));
        btnTestAdapt.setForeground(Color.WHITE);
        btnTestAdapt.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTestAdapt.addActionListener(e -> {
            int selectedRow = milestoneTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a milestone row from the table first.", "Select Milestone", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String inputScore = JOptionPane.showInputDialog(this, "Enter assessment score for this milestone (0 - 100%):", "75");
            if (inputScore != null) {
                try {
                    double score = Double.parseDouble(inputScore);
                    String milestoneId = currentRoadmap.getMilestones().get(selectedRow).getMilestoneId();
                    currentRoadmap = adaptiveEngine.adaptMilestone(currentRoadmap, milestoneId, score);
                    updateRoadmapTable();
                    JOptionPane.showMessageDialog(this, "Milestone updated with " + score + "%! Adaptive feedback applied.", "Roadmap Adapted", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid numeric score.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        actionPanel.add(btnTestAdapt);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateRoadmapTable() {
        if (currentRoadmap == null) return;

        milestoneTableModel.setRowCount(0);
        lblTotalDuration.setText("Total Duration: " + currentRoadmap.getTotalWeeks() + " Weeks");
        lblTotalHours.setText("Total Hours: " + currentRoadmap.getTotalEstimatedHours() + " Hours");
        lblOverallProgress.setText("Progress: " + currentRoadmap.getOverallProgress() + "%");

        for (Milestone m : currentRoadmap.getMilestones()) {
            String topResource = "-";
            if (!m.getRecommendedResources().isEmpty()) {
                Course c = m.getRecommendedResources().get(0);
                topResource = c.getTitle() + " (" + c.getProvider() + ")";
            }

            milestoneTableModel.addRow(new Object[]{
                m.getMilestoneId().toUpperCase(),
                m.getTopicName(),
                m.getCategory(),
                m.getDifficulty(),
                m.getEstimatedHours() + "h",
                "Wk " + m.getStartWeek() + " - " + m.getEndWeek(),
                m.getStatus(),
                m.getQuizScore() != null ? m.getQuizScore() + "%" : "Pending",
                topResource
            });
        }
    }

    private JPanel createAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(20, 25, 20, 25));
        panel.setBackground(Color.WHITE);

        JLabel header = new JLabel("Step 4: Prerequisite DAG Hierarchy & Category Analytics");
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(header, BorderLayout.NORTH);

        String[] cols = {"Topic ID", "Topic Name", "Domain", "Category", "Hours", "Prerequisite Dependencies (DAG)"};
        DefaultTableModel dagModel = new DefaultTableModel(cols, 0);

        for (Topic t : DataLoader.getAllTopics()) {
            List<String> prereqs = kg.getDirectPrerequisites(t.getId());
            dagModel.addRow(new Object[]{
                t.getId(),
                t.getName(),
                t.getDomain(),
                t.getCategory(),
                t.getEstimatedHours() + "h",
                prereqs.isEmpty() ? "Root Foundation" : String.join(" -> ", prereqs)
            });
        }

        JTable dagTable = new JTable(dagModel);
        dagTable.setRowHeight(26);
        dagTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dagTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        panel.add(new JScrollPane(dagTable), BorderLayout.CENTER);
        return panel;
    }
}
