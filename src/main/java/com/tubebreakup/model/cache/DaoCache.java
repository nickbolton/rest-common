package com.tubebreakup.model.cache;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class DaoCache<T> {

    private CacheManager cacheManager;
    private String namespace;
    private Cache noOpCache;

    @Getter
    @Setter
    private Boolean enabled = false;

    private Cache getCache() {
        if (!enabled) {
            return noOpCache;
        }
        Cache result = cacheManager.getCache(namespace);
        if (result == null) {
            result = noOpCache;
        }
        return result;
    }

    public T get(String key) {
        if (key == null) {
            return null;
        }
        Cache.ValueWrapper wrapper = getCache().get(key);
        if (wrapper == null) {
            return null;
        }
        return (T)wrapper.get();
    }

    public List<T> getList(String key) {
        if (key == null) {
            return Collections.emptyList();
        }
        Cache.ValueWrapper wrapper = getCache().get(key);
        if (wrapper == null) {
            return null;
        }
        Object result = wrapper.get();
        if (result instanceof List) {
            return (List<T>)result;
        }
        return null;
    }

    public List<List<T>> getDoubleList(String key) {
        if (key == null) {
            return Collections.emptyList();
        }
        Cache.ValueWrapper wrapper = getCache().get(key);
        if (wrapper == null) {
            return null;
        }
        Object result = wrapper.get();
        if (result instanceof List) {
            return (List<List<T>>)result;
        }
        return null;
    }

    public void put(CacheEntry<T> entry) {
        if (entry.getKey() == null) {
            return;
        }
        put(entry.getKey(), entry.getEntry());
    }

    public void put(String key, T entry) {
        if (key == null) {
            return;
        }
        if (entry != null) {
            if (log.isDebugEnabled()) {
                log.debug("CACHE PUT {}::{} {}", namespace, key, entry);
            }
            if (entry instanceof CacheableEntity) {
                ((CacheableEntity)entry).prepareForCaching();
            }
            getCache().put(key, entry);
        } else {
            remove(key);
        }
    }

    public void putOptional(CacheEntry<Optional<T>> entry) {
        if (entry.getKey() == null) {
            return;
        }
        putOptional(entry.getKey(), entry.getEntry());
    }

    public void putOptional(String key, Optional<T> entry) {
        if (key == null) {
            return;
        }
        if (entry != null && entry.isPresent()) {
            T entity = entry.get();
            if (log.isDebugEnabled()) {
                log.debug("CACHE PUT {}::{} {}", namespace, key, entity);
            }
            if (entity instanceof CacheableEntity) {
                ((CacheableEntity)entity).prepareForCaching();
            }
            getCache().put(key, entity);
        } else {
            remove(key);
        }
    }

    public void putList(CacheEntry<List<T>> entry) {
        if (entry.getKey() == null) {
            return;
        }
        putList(entry.getKey(), entry.getEntry());
    }

    public void putList(String key, List<T> list) {
        if (key == null) {
            return;
        }
        if (list != null) {
            if (log.isDebugEnabled()) {
                log.debug("CACHE PUT {}::{} {}", namespace, key, list);
            }
            for (T entity: list) {
                if (entity instanceof CacheableEntity) {
                    ((CacheableEntity)entity).prepareForCaching();
                }
            }
            getCache().put(key, list);
        } else {
            remove(key);
        }
    }

    public void putDoubleList(CacheEntry<List<List<T>>> entry) {
        if (entry.getKey() == null) {
            return;
        }
        putDoubleList(entry.getKey(), entry.getEntry());
    }

    public void putDoubleList(String key, List<List<T>> doubleList) {
        if (key == null) {
            return;
        }
        if (doubleList != null) {
            if (log.isDebugEnabled()) {
                log.debug("CACHE PUT {}::{} {}", namespace, key, doubleList);
            }
            for (List<T> list: doubleList) {
                for (T entity: list) {
                    if (entity instanceof CacheableEntity) {
                        ((CacheableEntity)entity).prepareForCaching();
                    }
                }
            }
            getCache().put(key, doubleList);
        } else {
            remove(key);
        }
    }

    public void remove(String key) {
        evict(key);
    }

    public void evictAll() {
        if (log.isDebugEnabled()) {
            log.debug("CACHE EVICT ALL {}", namespace);
        }
        getCache().clear();
    }

    public void evict(String key) {
        if (key == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("CACHE EVICT {}::{}", namespace, key);
        }
        getCache().evict(key);
    }

    public DaoCache(String namespace, CacheManager cacheManager) {
        this.namespace = namespace;
        this.cacheManager = cacheManager;
        noOpCache = new NoOpCache(namespace);
    }
}
