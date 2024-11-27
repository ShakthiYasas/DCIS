package org.dcis.cam.manager;

public final class ContextProviderManager {

    private static ContextProviderManager instance;

    private ContextProviderManager() {}

    public static synchronized ContextProviderManager getInstance() {
        if(instance == null) {
            instance = new ContextProviderManager();
        }
        return instance;
    }

    public void cacheProvider() {

    }

    public void getProvider() {

    }

    public void verifyBlueTooth() {

    }

}
