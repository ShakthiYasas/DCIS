package org.dcis.re.services;

import org.dcis.re.services.navigation.Graph;
import org.dcis.re.services.navigation.GraphNode;
import org.dcis.re.services.navigation.RouteFinder;

import java.util.List;
import java.util.ArrayList;

public class OptimalRouteService {
    public static void setVisited(String id) {
        Graph.getInstance().setVisited(id);
    }

    public static List<GraphNode> getRoute(String start, String end) {
        // RouteFinder navigator = new RouteFinder();
        // return navigator.findRoute(graph.getNode(start), graph.getNode(end));
        return new ArrayList<>();
    }

    public static List<GraphNode> getItinerary(List<GraphNode> preferred) {
        // RouteFinder navigator = new RouteFinder();
        // return navigator.findRoute(graph.getNode(start), graph.getNode(end));
        return new ArrayList<>();
    }
}
