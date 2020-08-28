package com.tubebreakup.model.cache;

import com.tubebreakup.model.BaseModel;
import com.tubebreakup.model.config.AppConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;

import java.util.List;

@Slf4j
public abstract class ResourceCache {

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

    public String compositeKey(String key) {
        if (key == null) {
            return null;
        }
        return new StringBuilder(namespace)
                .append(':').append(key)
                .toString();
    }

    public String compositeKey(String[] keys) {
        if (keys == null || keys.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (String key: keys) {
            if (builder == null) {
                builder = new StringBuilder(namespace).append(':');
            } else {
                builder.append('.');
            }
            builder.append(key);
        }
        return builder.toString();
    }

    public String compositeKey(BaseModel entity) {
        if (entity == null) {
            return null;
        }
        return new StringBuilder(namespace)
                .append(':').append(entity.getClass().getSimpleName())
                .append('-').append(entity.getUuid())
                .toString();
    }

    public String compositeKey(BaseModel[] entities) {
        if (entities == null || entities.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (BaseModel e: entities) {
            if (builder == null) {
                builder = new StringBuilder(namespace).append(':');
            } else {
                builder.append('.');
            }
            builder.append(e.getClass().getSimpleName()).append('-').append(e.getUuid());
        }
        return builder.toString();
    }

    public String compositeKey(BaseModel[] entities, String[] keys) {
        if (entities == null || entities.length <= 0 || keys == null || keys.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (BaseModel e: entities) {
            if (builder == null) {
                builder = new StringBuilder(namespace).append(':');
            } else {
                builder.append('.');
            }
            builder.append(e.getClass().getSimpleName()).append('-').append(e.getUuid());
        }
        for (String key: keys) {
            builder.append('.').append(key);
        }
        return builder.toString();
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

    public ResourceCache(String namespace, CacheManager cacheManager) {
        this.namespace = namespace;
        this.cacheManager = cacheManager;
        noOpCache = new NoOpCache(namespace);
    }
}
