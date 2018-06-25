package com.home.cc.replica.service.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.cc.replica.service.AppConstanst;
import com.home.cc.replica.service.ClusterController;
import com.home.cc.replica.service.LockHelper;
import com.home.cc.replica.service.ReplicaServiceException;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.List;

import static com.home.cc.replica.service.util.LambdaExceptionWrapper.throwingFunction;
import static java.util.stream.Collectors.toList;

@Component
public class ZkServiceRegistry implements ServiceRegistry, AppConstanst {
    private RegistryConfig config;
    private ZkClient zk;
    private ZkPath path;

    private LockHelper lock = new LockHelper();
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public ZkServiceRegistry(RegistryConfig config){
        this.config = config;
        path = new ZkPath(this.config.getZkRoot() );

        zk = new ZkClient( config.getZkConnections(), DEFAULT_ZK_SESSION_TIMEOUT, DEFAULT_ZK_CONNECTION_TIMEOUT, new StringByteSerializer() );

        if( !zk.exists(config.getZkRoot()) ){
            try {
                zk.createPersistent(config.getZkRoot());

            }catch (ZkNodeExistsException ex){
                //Somebody just created the root node before us. That is alright.
            }
        }

    }

    @PreDestroy
    public void close(){
        zk.close();
    }

    @Override
    public void addCluster(String name, String zkCons) {
        lock.executeWithLock( () -> {
            if( !zk.exists( path.cluster(name) )){
                zk.createPersistent( path.cluster(name), mapper.writeValueAsString( new ClusterInfo(name, zkCons)) );

            }else{
                throw new ReplicaServiceException("Cluster name " + name + " is already existed");
            }
        });
    }

    @Override
    public List<ClusterInfo> getAllClusters() {
        return zk.getChildren(path.root()).stream()
                .map( throwingFunction( cluster -> (String)zk.readData( path.cluster(cluster))) )
                .map( throwingFunction( info  -> mapper.readValue(info, ClusterInfo.class)) )
                .collect( toList());
    }
}
