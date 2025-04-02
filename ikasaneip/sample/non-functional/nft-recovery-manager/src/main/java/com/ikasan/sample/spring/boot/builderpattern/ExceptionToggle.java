package com.ikasan.sample.spring.boot.builderpattern;

public class ExceptionToggle {
    private static boolean throwRetryException = false;
    private static boolean throwStartRetryException = false;
    private static boolean shouldThrowScheduledRecoveryException = false;
    private static boolean shouldThrowExclusionException = false;
    private static boolean shouldThrowStoppedInErrorException = false;
    private static boolean shouldThrowRecoveryExceptionEveryNInvocations = false;
    private static int numberOfInvocationsBeforeRetry = 0;
    private static int counter = 0;
    private static int recoveryCount = 0;

    public static boolean isThrowRetryException() {
        return throwRetryException;
    }

    public static void setThrowRetryException(boolean throwRetryException) {
        ExceptionToggle.throwRetryException = throwRetryException;
    }

    public static boolean isThrowStartRetryException() {
        return throwStartRetryException;
    }

    public static void setThrowStartRetryException(boolean throwStartRetryException) {
        ExceptionToggle.throwStartRetryException = throwStartRetryException;
    }

    public static boolean isShouldThrowScheduledRecoveryException() {
        return shouldThrowScheduledRecoveryException;
    }

    public static void setShouldThrowScheduledRecoveryException(boolean shouldThrowScheduledRecoveryException) {
        ExceptionToggle.shouldThrowScheduledRecoveryException = shouldThrowScheduledRecoveryException;
    }

    public static boolean isShouldThrowExclusionException() {
        return shouldThrowExclusionException;
    }

    public static void setShouldThrowExclusionException(boolean shouldThrowExclusionException) {
        ExceptionToggle.shouldThrowExclusionException = shouldThrowExclusionException;
    }

    public static boolean isShouldThrowStoppedInErrorException() {
        return shouldThrowStoppedInErrorException;
    }

    public static void setShouldThrowStoppedInErrorException(boolean shouldThrowStoppedInErrorException) {
        ExceptionToggle.shouldThrowStoppedInErrorException = shouldThrowStoppedInErrorException;
    }

    public static boolean isShouldThrowRecoveryExceptionEveryNInvocations() {
        return shouldThrowRecoveryExceptionEveryNInvocations;
    }

    public static void setShouldThrowRecoveryExceptionEveryNInvocations(boolean shouldThrowRecoveryExceptionEveryNInvocations) {
        ExceptionToggle.shouldThrowRecoveryExceptionEveryNInvocations = shouldThrowRecoveryExceptionEveryNInvocations;
    }

    public static int getNumberOfInvocationsBeforeRetry() {
        return numberOfInvocationsBeforeRetry;
    }

    public static void setNumberOfInvocationsBeforeRetry(int numberOfInvocationsBeforeRetry) {
        ExceptionToggle.numberOfInvocationsBeforeRetry = numberOfInvocationsBeforeRetry;
    }

    public static int getCounter() {
        return counter;
    }

    public static void incrementCounter() {
        counter++;
    }

    public static int getRecoveryCount() {
        return recoveryCount;
    }

    public static void incrementRecoveryCounter() {
        recoveryCount++;
    }

    public synchronized static void reset() {
        throwRetryException = false;
        throwStartRetryException = false;
        shouldThrowScheduledRecoveryException = false;
        shouldThrowExclusionException = false;
        shouldThrowStoppedInErrorException = false;
        shouldThrowRecoveryExceptionEveryNInvocations = false;
        numberOfInvocationsBeforeRetry = 0;
        counter = 0;
        recoveryCount = 0;
    }
}
