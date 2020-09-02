package com.tubebreakup.model.cache;

import com.tubebreakup.model.config.AppConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;

import java.util.List;

@Slf4j
public abstract class DefaultResourceCache implements ResourceCache {

    private CacheManager cacheManager;
    private String namespace;
    private Cache noOpCache;

    protected abstract AppConfig getAppConfig();

    @Getter
    @Setter
    private Boolean enabled = true;

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

    public Object get(String key) {
        if (key == null) {
            return null;
        }
        return getCache().get(key, Object.class);
    }
    
    public void put(String key, Object entity) {
        if (key == null) {
            return;
        }
        if (entity == null) {
            getCache().evict(key);
        } else {
            getCache().put(key, entity);
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

    public Object getFromCacheWithFallback(String key, CacheEntryBuilder<Object> builder) {
        if (key == null) {
            return null;
        }
        Object entity = null;
        Boolean cacheEnabled = getAppConfig().getEntityCacheEnabled();
        if (cacheEnabled) {
            entity = getCache().get(key, Object.class);
            if (log.isDebugEnabled()) {
                if (entity != null) {
                    log.debug("CACHE HIT {}::{} {}", namespace, key, entity);
                } else {
                    log.debug("CACHE MISS {}::{}", namespace, key);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("CACHE DISABLED {}::{}", namespace, key);
            }
        }
        if (entity == null) {
            entity = updateCache(key, builder);
        }
        return entity;
    }

    public List<Object> getListFromCacheWithFallback(String key, CacheEntryBuilder<List<Object>> builder) {
        if (key == null) {
            return null;
        }
        List<Object> entity = null;
        Boolean cacheEnabled = getAppConfig().getEntityCacheEnabled();
        if (cacheEnabled) {
            entity = getCache().get(key, List.class);
            if (log.isDebugEnabled()) {
                if (entity != null) {
                    log.debug("CACHE HIT {}::{} {}", namespace, key, entity);
                } else {
                    log.debug("CACHE MISS {}::{}", namespace, key);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("CACHE DISABLED {}::{}", namespace, key);
            }
        }
        if (entity == null) {
            entity = updateCache(key, builder);
        }
        return entity;
    }

    protected <T> T updateCache(String key, CacheEntryBuilder<T> builder) {
        T entry = builder.build();
        getCache().put(key, entry);
        return entry;
    }

    public DefaultResourceCache(String namespace, CacheManager cacheManager) {
        this.namespace = namespace;
        this.cacheManager = cacheManager;
        noOpCache = new NoOpCache(namespace);
    }
}
