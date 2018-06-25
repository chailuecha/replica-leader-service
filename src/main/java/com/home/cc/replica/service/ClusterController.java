package com.home.cc.replica.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.cc.replica.service.registry.ClusterInfo;
import com.home.cc.replica.service.registry.ServiceRegistry;
import com.home.cc.replica.service.registry.ZkServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkTimeoutException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller()
@RequestMapping("/clusters/*")
public class ClusterController implements AppConstanst {
    private static Logger logger = Logger.getLogger(ClusterController.class);

    private ServiceRegistry registry;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ClusterController(ServiceRegistry registry){
        this.registry = registry;
    }

    @PutMapping("/{clusterName}")
    public ResponseEntity addCluster(@PathVariable("clusterName")  String name, @RequestParam("zkCons")  String zkCons){
        ResponseEntity testResp = testZKConnections(zkCons);
        if(testResp.getStatusCode() != HttpStatus.OK ){
            return testResp;
        }

        registry.addCluster(name, zkCons);
        return ResponseEntity.ok("");
    }

    private ResponseEntity testZKConnections(String zkCons){
        try {
            new ZkClient(zkCons, DEFAULT_ZK_SESSION_TIMEOUT, 5_000);

        }catch(ZkTimeoutException ex){
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Could not connect to zookeeper");
        }

        return ResponseEntity.ok("");
    }


    @GetMapping("/all")
    public ResponseEntity getAllClusters() throws JsonProcessingException {
        List<ClusterInfo> clusters = registry.getAllClusters();
        return ResponseEntity.ok( mapper.writeValueAsString(clusters) );
    }

    @DeleteMapping("{clusterName}")
    public ResponseEntity deleteCluster(@PathVariable("clusterName") String clusterName) throws JsonProcessingException {
        return ResponseEntity.ok("");
    }

    @ExceptionHandler({Exception.class})
    public @ResponseBody String handleException(HttpServletRequest req, Exception ex){
        logger.error("Error in cluster-controller",ex);
        return ex.getMessage();
    }

}
