package com.tubebreakup.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tubebreakup.util.ClassUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

@SuppressWarnings("serial")
@MappedSuperclass
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseModel implements Serializable {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @ApiModelProperty(hidden = true)
  @Column(length = 50)
  private String uuid;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  @ApiModelProperty(hidden = true)
  private Date createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated_at", nullable = false)
  @LastModifiedDate
  @ApiModelProperty(hidden = true)
  private Date updatedAt;

  public static <T extends BaseModel> T shallow(String uuid, Class<T> clazz) {
    T result = ClassUtils.getInstanceOf(clazz);
    result.setUuid(uuid);
    return result;
  }

  public final <T> void copyPatchableFields(T source, PropertySetterProvider setterProvider) {
    for(Field field  : getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Patchable.class)) {
          if (field.isAnnotationPresent(NotNull.class) || !field.getType().equals(String.class)) {
            setterProvider.setPropertyIfAvailable(this, source, field);
          } else {
            setterProvider.setNullablePropertyIfAvailable(this, source, field);
          }
        }
    }
  }

  public ShallowEntity shallowEntity() {
    return new ShallowEntity(getUuid());
  }

  public String formattedLabel(String labelIn, Integer width, Integer indent) {
    String indention = "";
    while (indention.length() < indent) {
      indention += " ";
    }
    String label = labelIn.toString();
    while (label.length() < width) {
      label = label + " ";
    }
    return indention + label;
  }

  protected String safeString(String s) {
    return s != null ? s : "";
  }

  protected String safeDate(Date d) {
    return d != null ? d.toString() : "";
  }

  protected String safeBoolean(Boolean b) {
    return b != null ? b.toString() : "false";
  }

  protected String safeObject(Object obj) {
    return obj != null ? obj.toString() : "";
  }

  protected String safeEntityId(BaseModel model) {
    return model != null ? model.getUuid() : "";
  }

  public BaseModel(String uuid) {
    this.uuid = uuid;
  }
}
