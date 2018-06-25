package com.home.cc.replica.service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockHelper {
    private Lock lock = new ReentrantLock();

    public <T> T produceWithLock(Producer<T> p) throws ReplicaServiceException {
        lock.lock();

        try{
            return p.produce();

        }catch (Exception e){
            throw new ReplicaServiceException(e);

        }finally {
            lock.unlock();
        }
    }

    public void executeWithLock(Execution exec) throws ReplicaServiceException {
        lock.lock();

        try {
            exec.execute();

        }catch (Exception e){
            throw new ReplicaServiceException(e);

        }finally {
            lock.unlock();
        }
    }

    public interface Producer<T>{
        T produce() throws Exception;
    }

    public interface Execution{
        void execute() throws Exception;
    }
}
