package org.dcis.ccm.cache;

import java.util.Optional;
import java.lang.ref.SoftReference;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ConcurrentHashMap;

public final class ContextCache implements CacheInterface {

    private Thread cleanerThread;
    private static ContextCache instance;
    private final DelayQueue<CacheObject> cleaningUpQueue;
    private final ConcurrentHashMap<String, SoftReference<Object>> cache;

    private ContextCache() {
        cleaningUpQueue = new DelayQueue<>();
        cache = new ConcurrentHashMap<>();
        initCleanerThread();
    }

    public static synchronized ContextCache getInstance() {
        if(instance == null) {
            instance = new ContextCache();
        }
        return instance;
    }

    private void initCleanerThread() {
        this.cleanerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                this.cleaningUpQueue.remove();
            }
        });
        this.cleanerThread.setDaemon(true);
        this.cleanerThread.start();
    }

    @Override
    public void add(String key, Object value) {
        if(key != null) {
            if(value == null) { cache.remove(key); }
            else {
                cache.put(key, new SoftReference<>(value));
            }
        }
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }

    @Override
    public Object get(String key) {
        return Optional.ofNullable(cache.get(key))
                .map(SoftReference::get).orElse(null);
    }

    @Override
    public boolean lookup(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public long size() {
        return cache.size();
    }

    @Override
    public void addGhost (String key) {
        SoftReference<Object> reference = new SoftReference<>(get(key));

        long expiryTime = System.currentTimeMillis() + 10000;
        cleaningUpQueue.put(new CacheObject(key, reference, expiryTime));
        remove(key);
    }
}
