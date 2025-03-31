package com.medcare.service;

import com.medcare.config.TestConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = TestConfig.class)
@ActiveProfiles("test")
public abstract class AbstractServiceTest {}