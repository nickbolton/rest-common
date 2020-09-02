package com.tubebreakup.model;

import com.tubebreakup.model.cache.CacheEntryBuilder;
import com.tubebreakup.model.config.AppConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
abstract public class BaseEntityDao<T extends BaseModel> implements EntityDao<T> {

    protected final String ALL_KEY = "ALL";

    @Autowired
    protected CacheManager cacheManager;

    @Autowired
    private DaoCache daoCache;

    @Getter
    @Setter
    private Boolean isCacheEnabled = false;

    protected abstract String getCacheNamespace();
    protected abstract String getEntityName();
    protected abstract AppConfig getAppConfig();
    protected abstract JpaRepository<T, String> getRepository();
    protected abstract void evictKeysForEntity(T entity);

    private Boolean getCacheEnabled() {
        return getAppConfig().getEntityCacheEnabled() && isCacheEnabled;
    }

    public String compositeKey(String key) {
        return new StringBuilder(getCacheNamespace()).append(':').append(getEntityName()).append(':').append(key).toString();
    }

    public String compositeKey(String[] keys) {
        if (keys == null || keys.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (String key : keys) {
            if (builder == null) {
                builder = new StringBuilder(getCacheNamespace()).append(':').append(getEntityName()).append(':');
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
        return new StringBuilder(getCacheNamespace())
                .append(':').append(getEntityName())
                .append('-').append(entity.getUuid())
                .toString();
    }

    public String compositeKey(BaseModel[] entities) {
        if (entities == null || entities.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (BaseModel e : entities) {
            if (builder == null) {
                builder = new StringBuilder(getCacheNamespace()).append(':').append(getEntityName()).append(':');
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
        for (BaseModel e : entities) {
            if (builder == null) {
                builder = new StringBuilder(getCacheNamespace()).append(':').append(getEntityName()).append(':');
            } else {
                builder.append('.');
            }
            builder.append(e.getClass().getSimpleName()).append('-').append(e.getUuid());
        }
        for (String key : keys) {
            builder.append('.').append(key);
        }
        return builder.toString();
    }

    public Optional<T> findById(String id) {
        if (!StringUtils.hasLength(id)) {
            return Optional.empty();
        }
        String key = compositeKey(id);
        return getFromCacheWithFallback(key, () -> fetchById(id));
    }

    public T fetchById(String id) {
        if (id == null) {
            return null;
        }
        Optional<T> optional = getRepository().findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    public List<T> fetchAll() {
        return getRepository().findAll();
    }

    public T save(String key, T entity) {
        evict(entity);
        if (key == null || entity == null) {
            if (getCacheEnabled() && key != null) {
                daoCache.evict(key);
            }
            return null;
        }
        T result = getRepository().save(entity);
        if (getCacheEnabled()) {
            daoCache.put(key, result);
        }
        return result;
    }

    public T save(T entity) {
        return save(compositeKey(entity), entity);
    }

    public void deleteById(String id) {
        Optional<T> optional = findById(id);
        if (optional.isPresent()) {
            delete(optional.get());
        }
        String key = compositeKey(id);
        if (getCacheEnabled()) {
            daoCache.evict(key);
        }
    }

    private <E> E performGet(String key, CacheEntryBuilder<E> builder) {
        if (key == null) {
            return null;
        }
        if (getCacheEnabled()) {
            E entity;
            Boolean[] didMiss = new Boolean[]{false};
            entity = daoCache.get(key, builder, didMiss);
            if (log.isDebugEnabled()) {
                if (didMiss[0]) {
                    log.debug("CACHE MISS {}::{}", daoCache.getCacheName(), key);
                } else {
                    log.debug("CACHE HIT {}::{} {}", daoCache.getCacheName(), key, entity);
                }
            }
            if (entity == null) {
                daoCache.evict(key);
            }
            return entity;
        }

        if (log.isDebugEnabled()) {
            log.debug("CACHE DISABLED {}::{}", daoCache.getCacheName(), key);
        }
        return builder.build();
    }

    public T getEntityFromCacheWithFallback(String key, CacheEntryBuilder<T> builder) {
        return performGet(key, builder);
    }

    public T getOptionalEntityFromCacheWithFallback(String key, CacheEntryBuilder<Optional<T>> builder) {
        if (key == null) {
            return null;
        }
        if (getCacheEnabled()) {
            T entity;
            Boolean[] didMiss = new Boolean[]{false};
            entity = daoCache.getOptional(key, builder, didMiss);
            if (log.isDebugEnabled()) {
                if (didMiss[0]) {
                    log.debug("CACHE MISS {}::{}", daoCache.getCacheName(), key);
                } else {
                    log.debug("CACHE HIT {}::{} {}", daoCache.getCacheName(), key, entity);
                }
            }
            if (entity == null) {
                daoCache.evict(key);
            }
            return entity;
        }

        if (log.isDebugEnabled()) {
            log.debug("CACHE DISABLED {}::{}", daoCache.getCacheName(), key);
        }

        Optional<T> optional = builder.build();
        return optional.isPresent() ? optional.get() : null;
    }

    public Optional<T> getFromCacheWithFallback(String key, CacheEntryBuilder<T> builder) {
        T entity = getEntityFromCacheWithFallback(key, builder);
        return entity != null ? Optional.of(entity) : Optional.empty();
    }

    public Optional<T> getOptionalFromCacheWithFallback(String key, CacheEntryBuilder<Optional<T>> builder) {
        T entity = getOptionalEntityFromCacheWithFallback(key, builder);
        return entity != null ? Optional.of(entity) : Optional.empty();
    }

    public List<T> getListFromCacheWithFallback(String key, CacheEntryBuilder<List<T>> builder) {
        if (key == null) {
            return Collections.emptyList();
        }
        List<T> list = performGet(key, builder);
        return list != null ? list : Collections.emptyList();
    }

    protected List<List<T>> getDoubleListFromCacheWithFallback(String key, CacheEntryBuilder<List<List<T>>> builder) {
        if (key == null) {
            return Collections.emptyList();
        }
        List<List<T>> list = performGet(key, builder);
        return list != null ? list : Collections.emptyList();
    }

    public List<T> findAll() {
        return getListFromCacheWithFallback(ALL_KEY, () -> fetchAll());
    }

    @Override
    public List<T> saveAll(Iterable<T> entities) {
        List<T> result = new LinkedList<>();
        for (T entity : entities) {
            result.add(save(entity));
        }
        return result;
    }

    @Override
    public List<T> findAllById(Iterable<String> ids) {
        List<T> result = new LinkedList<>();
        for (String id : ids) {
            Optional<T> optional = findById(id);
            if (optional.isPresent()) {
                result.add(optional.get());
            }
        }
        return result;
    }

    @Override
    public void deleteAll(Iterable<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    public void delete(T entity) {
        if (entity == null) {
            return;
        }
        evict(entity);
        getRepository().delete(entity);
    }

    public void deleteAll() {
        getRepository().deleteAll();
        evictAll();
    }

    public void evictAll() {
        getEntityCache().clear();
    }

    protected void evict(T entity) {
        if (entity == null || !getCacheEnabled()) {
            return;
        }
        daoCache.evict(compositeKey(entity.getUuid()));
        evictKeysForEntity(entity);
    }

    public void evict(String key) {
        if (getCacheEnabled()) {
            daoCache.evict(key);
        }
    }

    private Cache getEntityCache() {
        return cacheManager.getCache(daoCache.getCacheName());
    }
}
