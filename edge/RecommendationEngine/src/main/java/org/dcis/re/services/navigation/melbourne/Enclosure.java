package org.dcis.re.services.navigation.melbourne;

import org.dcis.re.services.navigation.GraphNode;

public class Enclosure implements GraphNode {
    private final String id;
    private boolean visited;
    private final String name;
    private final double latitude;
    private final double longitude;

    public Enclosure(String id, String name, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.visited = false;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setVisited() {
        this.visited = true;
    }

    public boolean getVisited() {
        return this.visited;
    }
}
