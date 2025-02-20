package org.dcis.re.services.navigation.melbourne;

import org.dcis.re.services.navigation.Graph;
import org.dcis.re.services.navigation.GraphNode;
import org.dcis.re.services.navigation.Scorer;

import java.util.Map;

public class CAScorer implements Scorer<GraphNode> {
    Map latencyDict;

    public CAScorer() {
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
