package org.ikasan.spec.scheduled.rest.agent.client.exception;

public class SchedulerAgentRestClientException extends RuntimeException {
    public SchedulerAgentRestClientException() {
    }

    public SchedulerAgentRestClientException(String message) {
        super(message);
    }

    public SchedulerAgentRestClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchedulerAgentRestClientException(Throwable cause) {
        super(cause);
    }

    public SchedulerAgentRestClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
