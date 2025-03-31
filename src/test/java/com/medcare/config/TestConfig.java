package com.medcare.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration
@ComponentScan(
        basePackages = "com.medcare",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.medcare.ui.*")
        }
)
public class TestConfig {}