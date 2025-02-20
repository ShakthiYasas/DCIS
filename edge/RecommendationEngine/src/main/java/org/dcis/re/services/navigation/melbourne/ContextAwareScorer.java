package org.dcis.re.services.navigation.melbourne;

import org.dcis.re.services.navigation.Graph;
import org.dcis.re.services.navigation.GraphNode;
import org.dcis.re.services.navigation.Scorer;

import java.util.HashMap;
import java.util.Map;

public class ContextAwareScorer implements Scorer<GraphNode> {
    Map latencyDict;

    public ContextAwareScorer() {
        latencyDict = Graph.getInstance().getLatencyDict();
    }

    @Override
    public double computeCost(GraphNode from, GraphNode to) {
        String key = from.getId() + to.getId();
        if(!latencyDict.containsKey(key))
            key = to.getId() + from.getId();
        return (double) latencyDict.get(key);
    }
}
