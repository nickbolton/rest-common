package com.tubebreakup.model;

import com.tubebreakup.model.config.AppConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
abstract public class BaseEntityDao<T extends BaseModel> implements EntityDao<T> {

  @Autowired
  protected CacheManager cacheManager;

  @Autowired
  protected AppConfigManager appConfigManager;

  protected abstract Cache getCache();

  protected abstract Optional<T> _findById(String id);

  protected abstract Optional<T> _fetchById(String id);

  protected abstract List<T> _findAll();

  public Optional<T> findById(String id) {
    if (!StringUtils.hasLength(id)) {
      return Optional.empty();
    }
    if (!appConfigManager.getAppConfig().getEntityCacheEnabled()) {
      log.debug("CACHE DISABLED {}", id);
      return _fetchById(id);
    }
    
    Optional<T> entity = _findById(id);
    if (entity != null) {
      log.debug("CACHE HIT {}", id);
      return entity;
    }
    
    log.debug("CACHE MISS {}", id);
    return _fetchById(id);
  }

  public List<T> findAll() {
    return _findAll();
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

  public void evictAll() {
    getCache().clear();
  }

  protected void evict(T entity) {
    if (entity == null) {
      return;
    }
    getCache().evict(entity.getUuid());
  }
}
