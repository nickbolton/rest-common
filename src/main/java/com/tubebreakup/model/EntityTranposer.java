package com.tubebreakup.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Component
public class EntityTranposer {

    private DateFormat dateFormat = new StdDateFormat();

    public <T> T transpose(T obj, FilterProvider filterProvider) {
        if (obj instanceof List) {
            return (T)transposeList((List)obj, filterProvider);
        }
        return (T)transposeObject(obj, filterProvider);
    }

    private List transposeList(List objects, FilterProvider filterProvider) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.setDateFormat(dateFormat);
        mapper.setFilterProvider(filterProvider);
        return mapper.convertValue(objects, new TypeReference<List<Map<String, Object>>>(){});
    }

    private Map transposeObject(Object object, FilterProvider filterProvider) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(dateFormat);
        mapper.setFilterProvider(filterProvider);
        return mapper.convertValue(object, new TypeReference<Map<String, Object>>(){});
    }
}
