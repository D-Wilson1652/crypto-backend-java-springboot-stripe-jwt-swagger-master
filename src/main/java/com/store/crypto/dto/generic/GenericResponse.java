package com.store.crypto.dto.generic;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericResponse {
    private Object data;
    private String message;
    private Integer statusCode;
    private Map<String, String> errors;  // Add this to capture field-specific error messages

}
