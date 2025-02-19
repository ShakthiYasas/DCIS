package org.dcis.re.services.navigation.melbourne;

import org.dcis.re.services.navigation.GraphNode;
import org.dcis.re.services.navigation.Scorer;

import java.util.HashMap;
import java.util.Map;

public class ContextAwareScorer implements Scorer<GraphNode> {
    private Map<String, Double> latencyDict;

    public ContextAwareScorer() {
        createLatencies();
    }

    @Override
    public double computeCost(GraphNode from, GraphNode to) {
        String key = from.getId() + to.getId();
        if(!latencyDict.containsKey(key))
            key = to.getId() + from.getId();
        return latencyDict.get(key);
    }

    private void createLatencies() {
        latencyDict = new HashMap<>();
        latencyDict.put("entexitmeerkat_enc",50.0);
        latencyDict.put("entexitnode1",39.1);
        latencyDict.put("node1node11",185.07);
        latencyDict.put("node11node10",30.0);
        latencyDict.put("node11elephant_enc",152.55);
        latencyDict.put("elephant_encnode10",27.9);
        latencyDict.put("node10orangutan_enc",138.98);
        latencyDict.put("node2orangutan_enc",66.4);
        latencyDict.put("node1node2",34.5);
        latencyDict.put("node2node3",33.0);
        latencyDict.put("node3node9",163.53);
        latencyDict.put("node3node4",30.89);
        latencyDict.put("node4node5",50.11);
        latencyDict.put("node5node6",42.05);
        latencyDict.put("node6node7",27.23);
        latencyDict.put("node8node9",69.36);
        latencyDict.put("node7node8",205.75);
        latencyDict.put("node7lion_enc",407.44);
        latencyDict.put("node6lion_enc",25.57);
        latencyDict.put("node5tortoise_enc",51.0);
        latencyDict.put("node7tortoise_enc",41.85);
        latencyDict.put("node8elephant_enc",115.98);
        latencyDict.put("node9elephant_enc",19.4);
        latencyDict.put("node4penguin_enc",55.57);
        latencyDict.put("node3bird_enc",63.46);
        latencyDict.put("node1bird_enc",155.92);
        latencyDict.put("node3penguin_enc",255.96);
    }
}
