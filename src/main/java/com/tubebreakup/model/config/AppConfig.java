package com.tubebreakup.model.config;

import com.tubebreakup.exception.CommonErrors;
import com.tubebreakup.exception.ErrorCodedHttpException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
@Setter
abstract public class AppConfig {

  protected Boolean httpLoggingEnabled = false;
  protected Long configTtl = 15l; // ttl in seconds
  protected Long registrationTokenExpiryMinutes = 1440l; // 24 hours
  protected Long resetPasswordTokenExpiryMinutes = 1440l; // 24 hours
  protected Long invitationTokenExpiryMinutes = 43200l; // 1 month
  protected Long trialExpiryMinutes = 43200l; // 1 month
  protected Long linkTokenExpiryMinutes = 43200l; // 1 month
  protected Boolean initializedDatabase = false;
  protected Boolean entityCacheEnabled = true;
  protected String webhookEndpointId = null;
  protected Boolean debugTime = false;
  protected Boolean mailDisabled = false;

  private Map<String, AppConfigValue> valueMap = new HashMap<>();
  private Map<String, Field> fieldMap = new HashMap<>();

  public void setValue(AppConfigValue value) {
    buildFieldMapIfNecessary();
    if (value == null) {
      return;
    }
    Field field = fieldMap.get(value.getName());
    if (field == null) {
      return;
    }
    try {
      String fieldName = field.getName();
      String properName = fieldName.substring(0, 1).toUpperCase() + (fieldName.length() > 1 ? fieldName.substring(1) : "");
      String getterName = "set" + properName;

      if (field.getType().equals(Long.class)) {
        Method setter = getClass().getMethod(getterName, Long.class);
        setter.invoke(this, value.longValue());
      } else if (field.getType().equals(Boolean.class)) {
        Method setter = getClass().getMethod(getterName, Boolean.class);
        setter.invoke(this, value.booleanValue());
      } else if (field.getType().equals(String.class)) {
        Method setter = getClass().getMethod(getterName, String.class);
        setter.invoke(this, value.getValue());
      } else if (field.getType().equals(Integer.class)) {
        Method setter = getClass().getMethod(getterName, Integer.class);
        setter.invoke(this, value.integerValue());
      } else {
        return;
      }
      valueMap.put(field.getName(), value);
    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  private List<Field> getAllFields() {
    final Field[] thisDeclaredFields = getClass().getDeclaredFields();
    final Field[] superDeclaredFields = AppConfig.class.getDeclaredFields();
    final List<Field> allFields = new LinkedList<>();
    allFields.addAll(Arrays.asList(thisDeclaredFields));
    allFields.addAll(Arrays.asList(superDeclaredFields));
    return allFields;
  }

  public List<AppConfigValue> toValues() {
    final Set<String> ignoredFields = new HashSet<String>(Arrays.asList("fieldMap", "valueMap"));
    final List<Field> allFields = getAllFields();
    if (valueMap.size() >= (allFields.size() - ignoredFields.size())) {
      return new LinkedList(valueMap.values());
    }
    List<AppConfigValue> result = new LinkedList<>();
    for (Field field : allFields) {
      if (ignoredFields.contains(field.getName())) {
        continue;
      }
      try {

        String fieldName = field.getName();
        String properName = fieldName.substring(0, 1).toUpperCase() + (fieldName.length() > 1 ? fieldName.substring(1) : "");
        String getterName = "get" + properName;

        Method sourceGetter = getClass().getMethod(getterName);
        Object fieldValue = sourceGetter.invoke(this);
        AppConfigValue value = new AppConfigValue();
        if (fieldValue != null) {
          value.setValue(fieldValue);
        } else {
          if (field.getType().equals(Long.class)) {
            value.setValue(0l);
          } else if (field.getType().equals(Boolean.class)) {
            value.setValue(false);
          } else if (field.getType().equals(Integer.class)) {
            value.setValue(0);
          }
        }
        value.setName(field.getName());
        result.add(value);
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  private void buildFieldMapIfNecessary() {
    if (fieldMap.size() > 0) {
      return;
    }
    final List<Field> allFields = getAllFields();
    for (Field field : allFields) {
      fieldMap.put(field.getName(), field);
    }
  }
}
