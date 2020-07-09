package com.tubebreakup.cache;

import com.tubebreakup.model.BaseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CacheController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected CacheManager cacheManager;

    public <T> Optional<T> get(String namespace, List<BaseModel> keys, Class<T> resultType) {
        if (!StringUtils.hasLength(namespace) || keys.size() <= 0) {
            return Optional.empty();
        }
        String key = buildKey(keys);
        Cache cache = getCacheFromInstances(namespace, keys);
        if (cache == null) {
            return Optional.empty();
        }
        T result = cache.get(key, resultType);
        if (result == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Cache miss {} {}", cache.getName(), key);
            }
            return Optional.empty();
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Cache hit {} {} {}", cache.getName(), key, result);
        }
        return Optional.of(result);
    }

    public void put(String namespace, List<BaseModel> keys, Object value) {
        if (!StringUtils.hasLength(namespace) || keys.size() <= 0) {
            return;
        }
        String key = buildKey(keys);
        Cache cache = getCacheFromInstances(namespace, keys);
        if (cache == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Cache put {} {} {}", cache.getName(), key, value);
        }
        cache.put(key, value);
    }

    private Cache getCacheFromInstances(String namespace, List<BaseModel> keys) {
        String cacheName = cacheNameFromInstances(namespace, keys);
        return getCache(cacheName);
    }

    private Cache getCache(String namespace, List<Class> keys) {
        String cacheName = cacheName(namespace, keys);
        return getCache(cacheName);
    }

    private Cache getCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("No cache named '{}'", cacheName);
            }
            return null;
        }
        return cache;
    }

    private String buildKey(List<BaseModel> keys) {
        StringBuilder builder = new StringBuilder();
        for (BaseModel model: keys) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(model.getUuid());
        }
        return builder.toString();
    }

    private String cacheName(String namespace, List<Class> keys) {
        return null;
    }

    private String cacheNameFromInstances(String namespace, List<BaseModel> keys) {
        List<Class> classKeys = keys.stream().map(m -> m.getClass()).collect(Collectors.toList());
        return cacheName(namespace, classKeys);
    }

    public void evict(String namespace, List<BaseModel> keys) {
        if (!StringUtils.hasLength(namespace) || keys.size() <= 0) {
            return;
        }
        String key = buildKey(keys);
        Cache cache = getCacheFromInstances(namespace, keys);
        if (cache == null) {
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Cache evict {} {} {}", cache.getName(), key);
        }
        cache.evict(key);
    }

    public void clear(String namespace, List<Class> keys) {
        Cache cache = getCache(namespace, keys);
        if (cache == null) {
            return;
        }
        cache.clear();
    }
}
