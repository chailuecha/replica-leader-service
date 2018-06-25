package com.home.cc.replica.service.registry;

import java.util.List;

public interface ServiceRegistry {
    void addCluster(String name, String zkCons);
    List<ClusterInfo> getAllClusters();

}
