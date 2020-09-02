package com.tubebreakup.model.cache;

import com.tubebreakup.model.BaseModel;

import java.util.List;

public interface ResourceCache {
    Object get(String key);
    void put(String key, Object entity);
    void remove(String key);
    void evictAll();
    void evict(String key);
    Object getFromCacheWithFallback(String key, CacheEntryBuilder<Object> builder);
    List<Object> getListFromCacheWithFallback(String key, CacheEntryBuilder<List<Object>> builder);
}
