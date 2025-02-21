package org.dcis.re.services;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RecommendationService {
    private Map<String, Map<String, Double>> loadModel() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("animal_model.ser"))) {
            return (Map<String, Map<String, Double>>) in.readObject();
        }
    }

    public String recommendForVisitor(String visitedAnimal, int top) throws IOException, ClassNotFoundException {
        Map<String, Map<String, Double>> animalPreferences = loadModel();
        JSONArray jarray = new JSONArray();

        Map<String, Double> similarAnimals = animalPreferences.get(visitedAnimal);
        if (similarAnimals != null) {
            List<Map.Entry<String, Double>> recommendations = similarAnimals.entrySet().stream()
                    .sorted((entry1, entry2) -> Double.compare(entry2.getValue(), entry1.getValue()))
                    .limit(top).toList();
            for(Map.Entry<String, Double> rec: recommendations) {
                JSONObject item = new JSONObject();
                item.put("destination", rec.getKey());
                item.put("route", OptimalRouteService.getRouteArray(visitedAnimal, rec.getKey()));
                item.put("confidence", rec.getValue());
                jarray.put(item);
            }
        }
        return jarray.toString();
    }
}
