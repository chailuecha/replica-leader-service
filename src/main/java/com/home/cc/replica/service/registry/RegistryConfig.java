package com.home.cc.replica.service.registry;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties("replica.service.registry")
public class RegistryConfig {
    private String zkConnections;
    private String zkRoot;

    public String getZkConnections() {
        return zkConnections;
    }

    public void setZkConnections(String zkConnections) {
        this.zkConnections = zkConnections;
    }

    public String getZkRoot() {
        return zkRoot;
    }

    public void setZkRoot(String zkRoot) {
        this.zkRoot = zkRoot;
    }
}
