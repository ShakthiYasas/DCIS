package org.dcis.cim.handler;

public class RecommenderWrapper {

    private static RecommenderWrapper instance;

    private RecommenderWrapper() {}

    public static synchronized RecommenderWrapper getInstance() {
        if(instance == null) {
            instance = new RecommenderWrapper();
        }
        return instance;
    }
}
