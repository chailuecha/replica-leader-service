package com.home.cc.replica.service.registry;

import java.util.List;

public interface ServiceRegistry {
    void createCluster(String name, String zkCons);
    void deleteCluster(String name);
    List<ClusterInfo> getAllClusters();

}
