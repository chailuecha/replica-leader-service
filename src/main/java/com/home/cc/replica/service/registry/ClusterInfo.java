package com.home.cc.replica.service.registry;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClusterInfo {
    private String name;
    private String zkCons;
    private String status;
    private long lastRun;

    public ClusterInfo(){

    }

    public ClusterInfo(String name, String zkCons){
        this.name = name;
        this.zkCons = zkCons;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getZkCons() {
        return zkCons;
    }

    public void setZkCons(String zkCons) {
        this.zkCons = zkCons;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastRun() {
        return lastRun;
    }

    public void setLastRun(long lastRun) {
        this.lastRun = lastRun;
    }

    public static void main(String[] args) throws IOException {
        String info = "{\"name\":\"localhost\",\"zkCons\":\"localhost:2181\",\"lastRun\":0}";
        ClusterInfo clusterInfo = new ObjectMapper().readValue(info, ClusterInfo.class);

        System.out.println(clusterInfo.getName());
    }
}
