package com.learningpath.core;

import com.learningpath.model.Topic;

import java.util.*;

public class KnowledgeGraph {
    private Map<String, Topic> topicMap;
    private Map<String, List<String>> adjList;      // u -> [v] where u is prerequisite for v
    private Map<String, List<String>> revAdjList;   // v -> [u] where u are prerequisites of v

    public KnowledgeGraph(List<Topic> topics) {
        this.topicMap = new HashMap<>();
        this.adjList = new HashMap<>();
        this.revAdjList = new HashMap<>();

        for (Topic t : topics) {
            topicMap.put(t.getId(), t);
            adjList.put(t.getId(), new ArrayList<>());
            revAdjList.put(t.getId(), new ArrayList<>());
        }

        for (Topic t : topics) {
            for (String prereq : t.getPrerequisites()) {
                if (topicMap.containsKey(prereq)) {
                    adjList.get(prereq).add(t.getId());
                    revAdjList.get(t.getId()).add(prereq);
                }
            }
        }
    }

    public Topic getTopic(String id) {
        return topicMap.get(id);
    }

    public List<Topic> getTopicsByDomain(String domain) {
        List<Topic> result = new ArrayList<>();
        for (Topic t : topicMap.values()) {
            if (t.getDomain().equalsIgnoreCase(domain)) {
                result.add(t);
            }
        }
        return result;
    }

    public List<String> getDirectPrerequisites(String topicId) {
        return revAdjList.getOrDefault(topicId, Collections.emptyList());
    }

    /**
     * Kahn's Algorithm for Topological Sorting of Topics in a given domain.
     * Ensures prerequisite topics mathematically precede dependent topics.
     */
    public List<Topic> getTopologicalSort(String domain) {
        List<Topic> domainTopics = getTopicsByDomain(domain);
        Set<String> domainIds = new HashSet<>();
        for (Topic t : domainTopics) {
            domainIds.add(t.getId());
        }

        // Calculate in-degrees within this domain
        Map<String, Integer> inDegree = new HashMap<>();
        for (String id : domainIds) {
            inDegree.put(id, 0);
        }

        for (String id : domainIds) {
            for (String prereq : revAdjList.getOrDefault(id, Collections.emptyList())) {
                if (domainIds.contains(prereq)) {
                    inDegree.put(id, inDegree.get(id) + 1);
                }
            }
        }

        // Enqueue nodes with in-degree 0 (root foundations)
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(
            id -> topicMap.get(id).getEstimatedHours()
        ));

        for (String id : domainIds) {
            if (inDegree.get(id) == 0) {
                queue.add(id);
            }
        }

        List<Topic> sortedTopics = new ArrayList<>();
        while (!queue.isEmpty()) {
            String curr = queue.poll();
            sortedTopics.add(topicMap.get(curr));

            for (String neighbor : adjList.getOrDefault(curr, Collections.emptyList())) {
                if (domainIds.contains(neighbor)) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.add(neighbor);
                    }
                }
            }
        }

        // If cycle or disconnected remaining nodes occur, append gracefully
        for (String id : domainIds) {
            if (!sortedTopics.contains(topicMap.get(id))) {
                sortedTopics.add(topicMap.get(id));
            }
        }

        return sortedTopics;
    }
}
