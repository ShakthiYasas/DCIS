package org.dcis.re.services.navigation;

import java.util.StringJoiner;

public class RouteNode <T extends GraphNode> implements Comparable<RouteNode> {
    private T previous;
    private final T current;
    private double routeScore;
    private double estimatedScore;

    RouteNode(T current) {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    RouteNode(T current, T previous, double routeScore, double estimatedScore) {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    T getCurrent() {
        return current;
    }

    T getPrevious() {
        return previous;
    }

    double getRouteScore() {
        return routeScore;
    }

    double getEstimatedScore() {
        return estimatedScore;
    }

    void setPrevious(T previous) {
        this.previous = previous;
    }

    void setRouteScore(double routeScore) {
        this.routeScore = routeScore;
    }

    void setEstimatedScore(double estimatedScore) {
        this.estimatedScore = estimatedScore;
    }

    @Override
    public int compareTo(RouteNode other) {
        return Double.compare(this.estimatedScore, other.estimatedScore);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RouteNode.class.getSimpleName() + "[", "]")
                .add("current=" + current)
                .add("previous=" + previous).add("routeScore=" + routeScore)
                .add("estimatedScore=" + estimatedScore)
                .toString();
    }
}
