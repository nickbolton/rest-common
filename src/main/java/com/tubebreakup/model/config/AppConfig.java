package com.tubebreakup.model.config;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.*;

@Getter
@Setter
public class AppConfig {

  private Boolean httpLoggingEnabled = false;
  private Long configTtl = 15l; // ttl in seconds
  private Long registrationTokenExpiryMinutes = 1440l; // 24 hours
  private Long resetPasswordTokenExpiryMinutes = 1440l; // 24 hours
  private Long invitationTokenExpiryMinutes = 43200l; // 1 month
  private Long trialExpiryMinutes = 43200l; // 1 month
  private Long linkTokenExpiryMinutes = 43200l; // 1 month
  private Boolean initializedDatabase = false;
  private Boolean entityCacheEnabled = true;
  private String webhookEndpointId = null;
  private Boolean debugTime = false;
  private Boolean mailDisabled = false;

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
      if (field.getType().equals(Long.class)) {
        field.set(this, value.longValue());
      } else if (field.getType().equals(Boolean.class)) {
        field.set(this, value.booleanValue());
      } else if (field.getType().equals(String.class)) {
        field.set(this, value.getValue());
      } else if (field.getType().equals(Integer.class)) {
        field.set(this, value.integerValue());
      } else {
        return;
      }
      valueMap.put(field.getName(), value);
    } catch (IllegalAccessException e) {
    }
  }

  public List<AppConfigValue> toValues() {
    if (valueMap.size() > 0) {
      return new LinkedList(valueMap.values());
    }
    final Set<String> ignoredFields = new HashSet<String>(Arrays.asList("fieldMap", "valueMap"));
    List<AppConfigValue> result = new LinkedList<>();
      for (Field field : getClass().getDeclaredFields()) {
        if (ignoredFields.contains(field.getName())) {
          continue;
        }
        try {
          Object fieldValue = field.get(this);
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
        } catch (IllegalAccessException e) {
        }
      }
      return result;
  }

  private void buildFieldMapIfNecessary() {
    if (fieldMap.size() > 0) {
      return;
    }
    for (Field field : getClass().getDeclaredFields()) {
      fieldMap.put(field.getName(), field);
    }
  }
}
