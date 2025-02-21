package org.dcis.re.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.dcis.re.services.navigation.Graph;
import org.dcis.re.services.navigation.GraphNode;
import org.dcis.re.services.navigation.RouteFinder;

import java.util.List;
import java.util.Map;

public class OptimalRouteService {
    public static void setVisited(String id) {
        Graph.getInstance().setVisited(id);
    }

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

    public static String getRoute(String start, String end) {
        return (getRouteArray(start,end)).toString();
    }

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
