package com.tubebreakup.model;

import com.tubebreakup.model.cache.CacheEntry;
import com.tubebreakup.model.cache.CacheEntryBuilder;
import com.tubebreakup.model.cache.DaoCache;
import com.tubebreakup.model.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
abstract public class BaseEntityDao<T extends BaseModel> implements EntityDao<T>, InitializingBean {

    protected final String ALL_KEY = "ALL";

    private DaoCache<T> cache;

    @Autowired
    protected CacheManager cacheManager;

    @PersistenceContext
    private EntityManager entityManager;

    protected abstract String getCacheName();
    protected abstract AppConfig getAppConfig();
    protected abstract JpaRepository<T, String> getRepository();
    protected abstract void evictKeysForEntity(T entity);

    @Override
    public void afterPropertiesSet() throws Exception {
        cache = new DaoCache<>(getCacheName(), cacheManager);
    }

    public Boolean getShouldCache() {
        return cache != null ? cache.getEnabled() : false;
    }

    public void setShouldCache(Boolean b) {
        if (cache == null) {
            return;
        }
        cache.setEnabled(b);
    }

    protected String compositeKey(String key) {
        if (key == null) {
            return null;
        }
        return new StringBuilder(getCacheName())
                .append(':').append(key)
                .toString();
    }

    protected String compositeKey(String[] keys) {
        if (keys == null || keys.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (String key: keys) {
            if (builder == null) {
                builder = new StringBuilder(getCacheName()).append(':');
            } else {
                builder.append('.');
            }
            builder.append(key);
        }
        return builder.toString();
    }

    protected String compositeKey(BaseModel entity) {
        if (entity == null) {
            return null;
        }
        return new StringBuilder(getCacheName())
                .append(':').append(entity.getClass().getSimpleName())
                .append('-').append(entity.getUuid())
                .toString();
    }

    protected String compositeKey(BaseModel[] entities) {
        if (entities == null || entities.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (BaseModel e: entities) {
            if (builder == null) {
                builder = new StringBuilder(getCacheName()).append(':');
            } else {
                builder.append('.');
            }
            builder.append(e.getClass().getSimpleName()).append('-').append(e.getUuid());
        }
        return builder.toString();
    }

    protected String compositeKey(BaseModel[] entities, String[] keys) {
        if (entities == null || entities.length <= 0 || keys == null || keys.length <= 0) {
            return null;
        }
        StringBuilder builder = null;
        for (BaseModel e: entities) {
            if (builder == null) {
                builder = new StringBuilder(getCacheName()).append(':');
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

    public T save(T entity) {
        if (entity == null) {
            return null;
        }
        evict(entity);
        return put(entity.getUuid(), getRepository().save(entity));
    }

    public void deleteById(String id) {
        Optional<T> optional = findById(id);
        if (optional.isPresent()) {
            delete(optional.get());
        }
        evict(id);
    }

    public T put(String key, T entity) {
        if (key == null) {
            return entity;
        }
        if (entity == null) {
            cache.evict(key);
            return null;
        }
        entityManager.detach(entity);
        cache.put(new CacheEntry<>(key, entity));
        return entity;
    }

    public void put(String key, Optional<T> optional) {
        if (key == null) {
            return;
        }
        if (optional == null || !optional.isPresent()) {
            cache.evict(key);
            return;
        }
        T entity = optional.get();
        entityManager.detach(entity);
        cache.put(new CacheEntry<>(key, entity));
    }

    public List<T> put(String key, List<T> entity) {
        if (key == null) {
            return entity;
        }
        if (entity == null) {
            cache.evict(key);
            return null;
        }
        detachList(entity);
        cache.putList(new CacheEntry<>(key, entity));
        return entity;
    }

    public List<List<T>> putDoubleList(String key, List<List<T>> entity) {
        if (key == null) {
            return entity;
        }
        if (entity == null) {
            cache.evict(key);
            return null;
        }
        detachDoubleList(entity);
        cache.putDoubleList(new CacheEntry<>(key, entity));
        return entity;
    }

    protected Optional<T> getFromCacheWithFallback(String key, CacheEntryBuilder<T> builder) {
        if (key == null) {
            return Optional.empty();
        }
        T entity = null;
        Boolean cacheEnabled = getAppConfig().getEntityCacheEnabled();
        if (cacheEnabled) {
            entity = cache.get(key);
            if (log.isDebugEnabled()) {
                if (entity != null) {
                    log.debug("CACHE HIT {}::{} {}", getCacheName(), key, entity);
                } else {
                    log.debug("CACHE MISS {}::{}", getCacheName(), key);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("CACHE DISABLED {}::{}", getCacheName(), key);
            }
        }
        if (entity != null) {
            entity = entityManager.merge(entity);
        } else {
            entity = updateCache(key, builder);
        }
        return entity != null ? Optional.of(entity) : Optional.empty();
    }

    protected Optional<T> getOptionalFromCacheWithFallback(String key, CacheEntryBuilder<Optional<T>> builder) {
        if (key == null) {
            return Optional.empty();
        }
        T entity = null;
        Boolean cacheEnabled = getAppConfig().getEntityCacheEnabled();
        if (cacheEnabled) {
            entity = cache.get(key);
            if (log.isDebugEnabled()) {
                if (entity != null) {
                    log.debug("CACHE HIT {}::{} {}", getCacheName(), key, entity);
                } else {
                    log.debug("CACHE MISS {}::{}", getCacheName(), key);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("CACHE DISABLED {}::{}", getCacheName(), key);
            }
        }
        if (entity != null) {
            entity = entityManager.merge(entity);
        } else {
            entity = updateCacheOptional(key, builder);
        }
        return entity != null ? Optional.of(entity) : Optional.empty();
    }

    protected List<T> getListFromCacheWithFallback(String key, CacheEntryBuilder builder) {
        if (key == null) {
            return Collections.emptyList();
        }
        List<T> entity = null;
        Boolean cacheEnabled = getAppConfig().getEntityCacheEnabled();
        if (cacheEnabled) {
            entity = cache.getList(key);
            if (log.isDebugEnabled()) {
                if (entity != null) {
                    log.debug("CACHE HIT {}::{} {}", getCacheName(), key, entity);
                } else {
                    log.debug("CACHE MISS {}::{}", getCacheName(), key);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("CACHE DISABLED {}::{}", getCacheName(), key);
            }
        }
        if (entity != null) {
            entity = mergeList(entity);
        } else {
            entity = updateCacheList(key, builder);
        }
        return entity != null ? entity : Collections.emptyList();
    }

    protected List<List<T>> getDoubleListFromCacheWithFallback(String key, CacheEntryBuilder builder) {
        if (key == null) {
            return Collections.emptyList();
        }
        List<List<T>> entity = null;
        Boolean cacheEnabled = getAppConfig().getEntityCacheEnabled();
        if (cacheEnabled) {
            entity = cache.getDoubleList(key);
            if (log.isDebugEnabled()) {
                if (entity != null) {
                    log.debug("CACHE HIT {}::{} {}", getCacheName(), key, entity);
                } else {
                    log.debug("CACHE MISS {}::{}", getCacheName(), key);
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("CACHE DISABLED {}::{}", getCacheName(), key);
            }
        }
        if (entity != null) {
            entity = mergeDoubleList(entity);
        } else {
            entity = updateCacheDoubleList(key, builder);
        }
        return entity != null ? entity : Collections.emptyList();
    }

    protected T updateCache(String key, CacheEntryBuilder<T> builder) {
        T entry = builder.build();
        if (entry != null) {
            entityManager.detach(entry);
        }
        cache.put(key, entry);
        return entry;
    }

    protected T updateCacheOptional(String key, CacheEntryBuilder<Optional<T>> builder) {
        Optional<T> entry = builder.build();
        if (entry != null && entry.isPresent()) {
            entityManager.detach(entry.get());
        }
        cache.putOptional(key, entry);
        return entry != null && entry.isPresent() ? entry.get() : null;
    }

    protected List<T> updateCacheList(String key, CacheEntryBuilder<List<T>> builder) {
        List<T> entry = builder.build();
        detachList(entry);
        cache.putList(key, entry);
        return entry != null ? entry : Collections.emptyList();
    }

    protected List<List<T>> updateCacheDoubleList(String key, CacheEntryBuilder<List<List<T>>> builder) {
        List<List<T>> entry = builder.build();
        detachDoubleList(entry);
        cache.putDoubleList(key, entry);
        return entry != null ? entry : Collections.emptyList();
    }

    public List<T> findAll() {
        return getListFromCacheWithFallback(ALL_KEY, () -> new CacheEntry(ALL_KEY, fetchAll()));
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
        cache.evictAll();
    }

    protected void evict(String key) {
        cache.evict(key);
    }

    protected void evict(T entity) {
        if (entity == null) {
            return;
        }
        cache.evict(compositeKey(entity.getUuid()));
        evictKeysForEntity(entity);
    }

    private void detachList(List<T> entities) {
        if (entities == null) {
            return;
        }
        for (T e: entities) {
            entityManager.detach(e);
        }
    }

    private void detachDoubleList(List<List<T>> entities) {
        if (entities == null) {
            return;
        }
        for (List<T> list: entities) {
            for (T e: list) {
                entityManager.detach(e);
            }
        }
    }

    private List<T> mergeList(List<T> entities) {
        if (entities == null) {
            return null;
        }
        List<T> result = new LinkedList<>();
        for (T e: entities) {
            result.add(entityManager.merge(e));
        }
        return result;
    }

    private List<List<T>> mergeDoubleList(List<List<T>> entities) {
        if (entities == null) {
            return null;
        }
        List<List<T>> result = new LinkedList<>();
        for (List<T> list: entities) {
            List<T> resultList = new LinkedList<>();
            for (T e: list) {
                resultList.add(entityManager.merge(e));
            }
            result.add(resultList);
        }
        return result;
    }
}
