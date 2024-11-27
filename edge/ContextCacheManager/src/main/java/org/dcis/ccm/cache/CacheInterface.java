package org.dcis.ccm.cache;

public interface CacheInterface {
    void add (String key, Object value);

    void addGhost (String key);

    boolean lookup (String key);

    void remove (String key);

    Object get (String key);

    void clear();

    long size();
}
