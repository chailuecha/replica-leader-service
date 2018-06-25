package com.home.cc.replica.service;

public class ReplicaServiceException extends RuntimeException {
    public ReplicaServiceException() {
    }

    public ReplicaServiceException(String message) {
        super(message);
    }

    public ReplicaServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReplicaServiceException(Throwable cause) {
        super(cause);
    }

    public ReplicaServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
