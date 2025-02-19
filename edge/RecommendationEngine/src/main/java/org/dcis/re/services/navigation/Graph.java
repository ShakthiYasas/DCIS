package org.dcis.re.services.navigation;

import org.dcis.re.services.navigation.melbourne.Enclosure;
import org.dcis.re.services.navigation.melbourne.Intersection;

import java.util.*;
import java.util.stream.Collectors;

public class Graph<T extends GraphNode> {
    private final Set<T> nodes;
    private final Map<String, Set<String>> connections;

    private static Graph instance = null;

    private Graph() {
        this.nodes = createNodes();
        this.connections = createConnections();
    }

    public static synchronized Graph getInstance() {
        if(instance == null)
            instance = new Graph();
        return instance;
    }

    public T getNode(String id) {
        return nodes.stream()
                .filter(node -> node.getId().equals(id) & isVisited(node))
                .findFirst()
                .orElseThrow(()
                        -> new IllegalArgumentException("No node found for the given ID."));
    }

    public void setVisited(String id) {
        T node = getNode(id);
        this.nodes.remove(node);
        ((Enclosure)node).setVisited();
        this.nodes.add(node);
    }

    public Set<T> getConnections(T node) {
        return connections.get(node.getId()).stream()
                .map(this::getNode)
                .collect(Collectors.toSet());
    }

    private boolean isVisited(T node) {
        if (node instanceof Enclosure){
            return !((Enclosure) node).getVisited();
        }
        return true;
    }

    private Set<T> createNodes() {
        Set<T> nodeSet = new HashSet<>();

        // Enclosures
        nodeSet.add((T) new Enclosure("lion_enc", "Lions", -37.78333333, 144.95166667));
        nodeSet.add((T) new Enclosure("koala_enc", "Koalas", -37.78444444, 144.95027778));
        nodeSet.add((T) new Enclosure("penguin_enc", "Penguins", -37.78388889, 144.95222222));
        nodeSet.add((T) new Enclosure("meerkat_enc", "Meerkats", -37.78472222, 144.95333333));
        nodeSet.add((T) new Enclosure("elephant_enc", "Elephants", -37.78583333, 144.94972222));
        nodeSet.add((T) new Enclosure("orangutan_enc", "Orangutans", -37.78527778, 144.95111111));
        nodeSet.add((T) new Enclosure("bird_enc", "Amazonian Birds", -37.78833333, 144.95277778));
        nodeSet.add((T) new Enclosure("tortoise_enc", "Giant Tortoises", -37.78333333, 144.95027778));

        // Intersections
        nodeSet.add((T) new Intersection("node1", -37.79722222, 144.95277778));
        nodeSet.add((T) new Intersection("node2", -37.78444444, 144.95222222));
        nodeSet.add((T) new Intersection("node3", -37.78416667, 144.95222222));
        nodeSet.add((T) new Intersection("node4", -37.78416667, 144.95166667));
        nodeSet.add((T) new Intersection("node5", -37.78361111, 144.95138889));
        nodeSet.add((T) new Intersection("node6", -37.78333333, 144.95111111));
        nodeSet.add((T) new Intersection("node7", -37.78277778, 144.95111111));
        nodeSet.add((T) new Intersection("node8", -37.78388889, 144.94972222));
        nodeSet.add((T) new Intersection("node9", -37.78416667, 144.95027778));
        nodeSet.add((T) new Intersection("node10", -37.78583333, 144.95000000));
        nodeSet.add((T) new Intersection("node11", -37.78527778, 144.95083333));
        nodeSet.add((T) new Intersection("entexit", -37.78527778, 144.95305556));

        return nodeSet;
    }

    private Map<String, Set<String>> createConnections() {
        Map<String, Set<String>> map = new HashMap<>();

        map.put("entexit", new HashSet<>(List.of("meerkat_enc", "node1")));
        map.put("meerkat_enc", new HashSet<>(List.of("entexit")));
        map.put("node1", new HashSet<>(List.of("entexit", "node2", "node11", "bird_enc")));
        map.put("node2", new HashSet<>(List.of("node1", "node3")));
        map.put("node3", new HashSet<>(List.of("node2", "node4", "node9")));
        map.put("bird_enc", new HashSet<>(List.of("node1", "node3")));
        map.put("penguin_enc", new HashSet<>(List.of("node3")));
        map.put("node11", new HashSet<>(List.of("node10", "elephant_enc")));
        map.put("elephant_enc", new HashSet<>(List.of("node10", "node11")));
        map.put("node10", new HashSet<>(List.of("node11", "elephant_enc", "orangutan_enc")));
        map.put("orangutan_enc", new HashSet<>(List.of("node10", "node2")));
        map.put("node9", new HashSet<>(List.of("node8", "node3", "koala_enc")));
        map.put("node8", new HashSet<>(List.of("node9", "node7", "koala_enc")));
        map.put("koala_enc", new HashSet<>(List.of("node9", "node8")));
        map.put("node4", new HashSet<>(List.of("node3", "node5", "penguin_enc")));
        map.put("node5", new HashSet<>(List.of("node4", "node6", "tortoise_enc")));
        map.put("tortoise_enc", new HashSet<>(List.of("node5", "node7")));
        map.put("node7", new HashSet<>(List.of("node8", "node6", "tortoise_enc")));
        map.put("node6", new HashSet<>(List.of("node7", "node5", "lion_enc")));
        map.put("lion_enc", new HashSet<>(List.of("node7")));

        return map;
    }
}
