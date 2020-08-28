package com.tubebreakup.model.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CacheEntry<T> {
    private String key;
    private T entry;
}
