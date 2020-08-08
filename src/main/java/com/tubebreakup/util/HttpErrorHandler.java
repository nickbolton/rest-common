package com.tubebreakup.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

@Component
public class HttpErrorHandler {
    public HttpErrorResponse parseErrorResponse(HttpClientErrorException exception) {
        if (exception == null) {
            return null;
        }
        String responseBody = exception.getResponseBodyAsString();
        if (!StringUtils.hasLength(responseBody)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(responseBody, HttpErrorResponse.class);
        } catch (IOException e) {
            return null;
        }
    }
}
