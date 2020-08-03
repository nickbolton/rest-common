package com.tubebreakup.model.config;

import com.tubebreakup.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class AppConfigValue extends BaseModel {
    private static final long serialVersionUID = 2367543016384257535L;

    @Column(unique = true)
    private String name;
    private String value;

    public void setValue(Object value) {
        this.value = value.toString();
    }

    public Integer integerValue() {
        try {
            Integer result = Integer.parseInt(value);
            return result;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public Long longValue() {
        try {
            Long result = Long.parseLong(value);
            return result;
        } catch (NumberFormatException e) {
            return 0l;
        }
    }

    public Boolean booleanValue() {
        try {
            Boolean result = Boolean.parseBoolean(value);
            return result;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
