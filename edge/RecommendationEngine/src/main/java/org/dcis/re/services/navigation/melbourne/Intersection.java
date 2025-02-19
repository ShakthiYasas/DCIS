package org.dcis.re.services.navigation.melbourne;

import org.dcis.re.services.navigation.GraphNode;

public class Intersection implements GraphNode {
    private final String id;
    private final double latitude;
    private final double longitude;

    public Intersection(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}

