package com.tubebreakup.model;

import java.lang.reflect.Field;

public interface PropertySetterProvider {
  public <T> void setPropertyIfAvailable(T target, T source, String name);
  public <T> void setNullablePropertyIfAvailable(T target, T source, String name);
  public <T> void setPropertyIfAvailable(T target, T source, Field field);
  public <T> void setNullablePropertyIfAvailable(T target, T source, Field field);
}
