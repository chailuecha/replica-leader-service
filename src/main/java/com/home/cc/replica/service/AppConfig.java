package com.home.cc.replica.service;

import com.home.cc.replica.service.registry.RegistryConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(RegistryConfig.class)
public class AppConfig {

}
