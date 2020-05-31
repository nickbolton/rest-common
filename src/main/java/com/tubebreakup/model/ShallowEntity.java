package com.tubebreakup.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShallowEntity {

    private String uuid;

    protected ShallowEntity() {
        super();
    }
}