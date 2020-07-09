package com.tubebreakup.model;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class EntityKeyGenerator implements KeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    return (params);
  }

  public static Object generateKey(Object... params) {
    if (params.length == 0) {
      return SimpleKey.EMPTY;
    }
    List<BaseModel> entities = new ArrayList<>();
    for (Object obj : params) {
      if (!(obj instanceof BaseModel)) {
        return generateSimpleKey(params);
      }
      entities.add((BaseModel) obj);
    }

    entities.sort(new Comparator<BaseModel>() {
      @Override
      public int compare(BaseModel o1, BaseModel o2) {
        return o1.getUuid().compareTo(o2.getUuid());
      }
    });
    StringBuilder key = new StringBuilder();
    for (BaseModel entity : entities) {
      if (key.length() > 0) {
        key.append(',');
      }
      key.append(entity.getUuid());
    }
    return key.toString();
  }

  public static Object generateSimpleKey(Object... params) {
    if (params.length == 0) {
      return SimpleKey.EMPTY;
    }
    if (params.length == 1) {
      Object param = params[0];
      if (param != null && !param.getClass().isArray()) {
        return param;
      }
    }
    return new SimpleKey(params);
  }
}
