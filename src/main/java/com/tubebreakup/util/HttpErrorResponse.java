package com.tubebreakup.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonSerialize
public class HttpErrorResponse {
    @JsonProperty private String error;
    @JsonProperty("error_description") private String errorDescription;
}
