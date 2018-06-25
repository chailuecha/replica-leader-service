package com.home.cc.replica.service.registry;

public class ZkPath {
    private String root;

    public ZkPath(String root){
        this.root = root;
    }

    public String root(){
        return this.root;
    }

    public String cluster(String clusterName){
        return root + "/" + clusterName;
    }
}
