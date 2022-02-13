package org.ikasan.ootb.scheduler.agent.module.service;

public class JobProvisionServiceException extends RuntimeException {
    public JobProvisionServiceException() {
    }

    public JobProvisionServiceException(String message) {
        super(message);
    }

    public JobProvisionServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobProvisionServiceException(Throwable cause) {
        super(cause);
    }

    public JobProvisionServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
