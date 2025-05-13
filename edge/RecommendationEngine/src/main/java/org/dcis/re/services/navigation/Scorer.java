package org.dcis.re.services.navigation;

import java.util.Set;

public interface Scorer<T extends GraphNode> {
    double computeCost(T from, T to);
}
