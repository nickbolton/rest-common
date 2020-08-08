package com.tubebreakup.filter;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonSerialize
public class RequestPayload {
    private String uri;
    private HttpHeaders headers;
    private String client;
    private String session;
    private String user;
    private String rawPayload;
    private Map<String, ?> jsonPayload;
}
