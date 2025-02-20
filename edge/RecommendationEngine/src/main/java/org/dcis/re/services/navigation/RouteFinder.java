package org.dcis.re.services.navigation;

import java.util.*;
import java.util.stream.Collectors;

public class RouteFinder <T extends GraphNode> {
    private final Graph<T> graph;
    private final Scorer<T> targetScorer;
    private final Scorer<T> nextNodeScorer;

    public RouteFinder(Graph<T> graph, Scorer<T> nextNodeScorer, Scorer<T> targetScorer) {
        this.graph = graph;
        this.targetScorer = targetScorer;
        this.nextNodeScorer = nextNodeScorer;
    }

    // 1. Finding the shortest path between two nodes.
    public List<T> findRoute(T from, T to) {
        Map<T, RouteNode<T>> allNodes = new HashMap<>();
        Queue<RouteNode> openSet = new PriorityQueue<>();

        RouteNode<T> start = new RouteNode<>(
                from, null, 0d, targetScorer.computeCost(from, to));
        allNodes.put(from, start);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            RouteNode<T> next = openSet.poll();
            if (next.getCurrent().equals(to)) {
                List<T> route = new ArrayList<>();
                RouteNode<T> current = next;
                do {
                    route.add(0, current.getCurrent());
                    current = allNodes.get(current.getPrevious());
                } while (current != null);

                return route;
            }

            graph.getConnections(next.getCurrent()).forEach(connection -> {
                double newScore = next.getRouteScore() +
                        nextNodeScorer.computeCost(next.getCurrent(), connection);
                RouteNode<T> nextNode = allNodes.getOrDefault(connection, new RouteNode<>(connection));
                allNodes.put(connection, nextNode);

                if (nextNode.getRouteScore() > newScore) {
                    nextNode.setPrevious(next.getCurrent());
                    nextNode.setRouteScore(newScore);
                    nextNode.setEstimatedScore(newScore + targetScorer.computeCost(connection, to));
                    openSet.add(nextNode);
                }
            });
        }

        throw new IllegalStateException("No route found.");
    }

    // 2. The initial itinerary generation for a user.
    public List<T> generateItinerary(T from, Map<T,Integer> preferred) {
        List<T> nodesToVisit = new ArrayList<>(preferred.keySet());
        nodesToVisit.sort(Comparator.comparingInt(preferred::get));

        List<T> allNodes = new ArrayList<>(nodesToVisit);
        allNodes.add(from);

        Map<T, Map<String, Double>> shortestPaths = new HashMap<>();
        for (T node : allNodes) shortestPaths.put(node, dijkstra(node));

        List<List<String>> permutations = generatePermutations(nodesToVisit);
        double minLatency = Double.MAX_VALUE;
        List<String> minPath = null;

        for (List<String> perm : permutations) {
            List<String> path = new ArrayList<>();
            path.add(from.getId());
            path.addAll(perm);
            path.add(from.getId());

            double latency = calculatePathLatency(path, shortestPaths);
            if (latency < minLatency) {
                minPath = path;
                minLatency = latency;
            }
        }

        assert minPath != null;
        return minPath.stream()
                .map(graph::getNode).collect(Collectors.toList());
    }

    static class Node {
        String name;
        double latency;

        Node(String name, double latency) {
            this.name = name;
            this.latency = latency;
        }
    }

    private Map<String, Double> dijkstra(T start) {
        Map<String, Double> latencies = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(
                Comparator.comparingDouble(node -> node.latency));

        for (Object node: graph.getAllNodes())
            latencies.put(((T)node).getId(), Double.MAX_VALUE);

        latencies.put(start.getId(), 0.0);
        pq.add(new Node(start.getId(), 0.0));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            String currentNode = current.name;
            double currentLatency = current.latency;

            if (currentLatency > latencies.get(currentNode)) continue;

            for (Object neighbor : graph.getConnections(currentNode)) {
                String neighborNode = ((T)neighbor).getId();
                double edgeLatency = graph.getLatency(currentNode, neighborNode);
                double newLatency = currentLatency + edgeLatency;

                if (newLatency < latencies.get(neighborNode)) {
                    latencies.put(neighborNode, newLatency);
                    pq.add(new Node(neighborNode, newLatency));
                }
            }
        }

        return latencies;
    }

    private List<List<String>> generatePermutations(List<T> nodes) {
        List<List<String>> result = new ArrayList<>();
        if (nodes.isEmpty()) {
            result.add(new ArrayList<>());
            return result;
        }

        String firstNode = nodes.getFirst().getId();
        List<T> remainingNodes = nodes.subList(1, nodes.size());
        List<List<String>> permutationsOfRemainder = generatePermutations(remainingNodes);

        for (List<String> perm : permutationsOfRemainder) {
            for (int i = 0; i <= perm.size(); i++) {
                List<String> temp = new ArrayList<>(perm);
                temp.add(i, firstNode);
                result.add(temp);
            }
        }
        return result;
    }

    private double calculatePathLatency(List<String> path, Map<T, Map<String, Double>> shortestPaths) {
        double latency = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            String fromNode = path.get(i);
            String toNode = path.get(i + 1);
            latency += shortestPaths.get(graph.getNode(fromNode)).get(toNode);
        }
        return latency;
    }
}
