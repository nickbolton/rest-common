package com.tubebreakup.model.cache;

import com.tubebreakup.model.BaseModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DaoCache {

    private final String ENTITY_CACHE_NAME = "EntityCache";

    public String getCacheName() {
        return ENTITY_CACHE_NAME;
    }

    @Cacheable(cacheNames = ENTITY_CACHE_NAME, key = "#key", unless = "#result == null")
    public <T> T get(String key, CacheEntryBuilder<T> builder, Boolean[] didMiss) {
        if (didMiss != null && didMiss.length > 0) {
            didMiss[0] = true;
        }
        return builder.build();
    }

    @Cacheable(cacheNames = ENTITY_CACHE_NAME, key = "#key", unless = "#result == null")
    public <T> T getOptional(String key, CacheEntryBuilder<Optional<T>> builder, Boolean[] didMiss) {
        if (didMiss != null && didMiss.length > 0) {
            didMiss[0] = true;
        }
        Optional<T> optional = builder.build();
        return optional.isPresent() ? optional.get() : null;
    }

    @CachePut(cacheNames = ENTITY_CACHE_NAME, key = "#key", unless = "#result == null")
    public <T extends BaseModel> T put(String key, T entity) {
        return entity;
    }

    @CachePut(cacheNames = ENTITY_CACHE_NAME, key = "#key", unless = "#result == null")
    public <T extends BaseModel> List<T> putList(String key, List<T> list) {
        return list;
    }

    @CachePut(cacheNames = ENTITY_CACHE_NAME, key = "#key", unless = "#result == null")
    public <T extends BaseModel> List<List<T>> putDoubleList(String key, List<List<T>> doubleList) {
        return doubleList;
    }

    @CacheEvict(cacheNames = ENTITY_CACHE_NAME, key = "#key")
    public void evict(String key) {
    }
}
