package org.dcis.re.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.dcis.re.services.navigation.Graph;
import org.dcis.re.services.navigation.GraphNode;
import org.dcis.re.services.navigation.RouteFinder;

import java.util.List;
import java.util.Map;

public class OptimalRouteService {

    // Flags an enclosure as visited.
    // tag: Enclosure tag of the last visited enclosure.
    // returns: None.
    public static void setVisited(String tag) {
        Graph.getInstance().setVisited(tag);
    }

    // Calculates and shares the initial and subsequent alternate itineraries.
    // from: The last visited animal enclosure tag or the identifier of the current node.
    // preferred: The map of enclosure tags and relative priority based on the current situation.
    // oneWay: Whether or not the visitor needs to return to the current node. True is not returning.
    // returns: String converted JSON Array of enclosures and the route.
    public static String getItinerary(String from, Map<String,Integer> preferred, Boolean oneway) {
        RouteFinder<?> navigator = new RouteFinder<>(Graph.getInstance());
        List<?> itinerary = navigator.generateItinerary(from, preferred, oneway);

        JSONArray response = new JSONArray();
        for(Object node: itinerary){
            JSONObject nodeItem = new JSONObject();
            nodeItem.put("id", ((GraphNode)node).getId());
            nodeItem.put("latitude", ((GraphNode)node).getLatitude());
            nodeItem.put("longitude", ((GraphNode)node).getLongitude());
            response.put(nodeItem);
        }

        return response.toString();
    }

    // Calculates the shortest path between two given enclosures.
    // start: The starting animal enclosure tag.
    // end: The destination animal enclosure tag.
    // returns: String converted JSON Array of enclosures and the route from the start.
    public static String getRoute(String start, String end) {
        return (getRouteArray(start,end)).toString();
    }

    // Calculates the shortest path between two given enclosures.
    // start: The starting animal enclosure tag.
    // end: The destination animal enclosure tag.
    // returns: JSON Array of enclosures and the route from the start.
    public static JSONArray getRouteArray(String start, String end) {
        RouteFinder<?> navigator = new RouteFinder<>(Graph.getInstance());
        List<?> route = navigator.findRoute(start, end);

        JSONArray response = new JSONArray();
        for(Object node: route){
            JSONObject nodeItem = new JSONObject();
            nodeItem.put("id", ((GraphNode)node).getId());
            nodeItem.put("latitude", ((GraphNode)node).getLatitude());
            nodeItem.put("longitude", ((GraphNode)node).getLongitude());
            response.put(nodeItem);
        }

        return response;
    }
}
