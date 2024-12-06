package org.dcis.ccm.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class CacheObject implements Delayed {

    private final String key;
    private final long expiryTime;
    private final SoftReference<Object> reference;

    public CacheObject(String key, SoftReference<Object> reference, long expiryTime) {
        this.key = key;
        this.reference = reference;
        this.expiryTime = expiryTime;
    }

    public String getKey() {
        return key;
    }

    public SoftReference<Object> getReference () {
        return reference;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(expiryTime - System.currentTimeMillis(),
                TimeUnit.MILLISECONDS);
    }

    public int compareTo(Delayed item) {
        return Long.compare(expiryTime, ((CacheObject) item).expiryTime);
    }

}
