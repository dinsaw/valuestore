package com.github.dinsaw.valuestore.service;

import com.github.dinsaw.valuestore.dto.AppGreeting;

/**
 * Created by dinsaw on 7/12/18.
 */
public class InfoService {
    public AppGreeting greet() {
        return AppGreeting.builder()
                .greeting("Hello, Welcome to ValueStore :)")
                .version("0.1")
                .description("ValueStore is REST Backend to store and retrieve mutual fund net asset values. " +
                        "It fetches net asset values from multiple sources and stores it in standardized manner.")
                .sourceCode("https://github.com/dinsaw/valuestore")
                .build();
    }
}
