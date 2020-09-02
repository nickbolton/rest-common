package com.tubebreakup.service;

import com.tubebreakup.model.BaseModel;

import java.util.List;
import java.util.Optional;

public interface EntityService<T extends BaseModel> {
  
  public T save(T entity);

  public List<T> saveAll(Iterable<T> entities);

  public Optional<T> findById(String id);

  public List<T> findAllById(Iterable<String> ids);

  public List<T> findAll();

  public void deleteById(String id);

  public void delete(T entity);

  public void deleteAll(Iterable<T> entities);

  public void deleteAll();  
}
