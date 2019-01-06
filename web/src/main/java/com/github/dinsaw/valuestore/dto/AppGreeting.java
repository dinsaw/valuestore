package com.github.dinsaw.valuestore.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Created by dinsaw on 7/12/18.
 */
@Builder
@Data
public class AppGreeting {
    private String version;
    private String greeting;
    private String description;
    private String sourceCode;
}
