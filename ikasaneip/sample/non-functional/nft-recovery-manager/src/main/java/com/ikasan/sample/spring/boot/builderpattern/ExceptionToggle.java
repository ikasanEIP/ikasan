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

    /**
     * Returns the value of the throwRetryException boolean flag.
     *
     * @return boolean value of the throwRetryException flag
     */
    public static boolean isThrowRetryException() {
        return throwRetryException;
    }

    /**
     * Sets the flag to determine whether to throw a retry exception or not.
     *
     * @param throwRetryException true to throw retry exception, false otherwise
     */
    public static void setThrowRetryException(boolean throwRetryException) {
        ExceptionToggle.throwRetryException = throwRetryException;
    }

    /**
     * Determines if the application should throw a StartRetryException.
     *
     * @return true if the application should throw a StartRetryException, false otherwise
     */
    public static boolean isThrowStartRetryException() {
        return throwStartRetryException;
    }

    /**
     * Set whether to throw a start retry exception.
     *
     * @param throwStartRetryException a boolean indicating whether to throw a start retry exception or not
     */
    public static void setThrowStartRetryException(boolean throwStartRetryException) {
        ExceptionToggle.throwStartRetryException = throwStartRetryException;
    }

    /**
     * Indicates whether the application should throw a scheduled recovery exception.
     *
     * @return true if the application should throw a scheduled recovery exception, false otherwise
     */
    public static boolean isShouldThrowScheduledRecoveryException() {
        return shouldThrowScheduledRecoveryException;
    }

    /**
     * Sets whether the system should throw a scheduled recovery exception.
     *
     * @param shouldThrowScheduledRecoveryException true if the system should throw a scheduled recovery exception, false otherwise
     */
    public static void setShouldThrowScheduledRecoveryException(boolean shouldThrowScheduledRecoveryException) {
        ExceptionToggle.shouldThrowScheduledRecoveryException = shouldThrowScheduledRecoveryException;
    }

    /**
     *
     */
    public static boolean isShouldThrowExclusionException() {
        return shouldThrowExclusionException;
    }

    /**
     * Sets whether an exclusion exception should be thrown during the flow execution.
     *
     * @param shouldThrowExclusionException true if an exclusion exception should be thrown, false otherwise
     */
    public static void setShouldThrowExclusionException(boolean shouldThrowExclusionException) {
        ExceptionToggle.shouldThrowExclusionException = shouldThrowExclusionException;
    }

    /**
     * Retrieves the current value of the flag indicating whether an error should be thrown when stopped in error.
     *
     * @return true if an error should be thrown when stopped in error, false otherwise
     */
    public static boolean isShouldThrowStoppedInErrorException() {
        return shouldThrowStoppedInErrorException;
    }

    /**
     * Sets whether to throw a stopped in error exception when specific error conditions are met.
     *
     * @param shouldThrowStoppedInErrorException true to throw stopped in error exception, false otherwise
     */
    public static void setShouldThrowStoppedInErrorException(boolean shouldThrowStoppedInErrorException) {
        ExceptionToggle.shouldThrowStoppedInErrorException = shouldThrowStoppedInErrorException;
    }

    /**
     * Returns a boolean indicating whether the recovery exception should be thrown every N invocations.
     *
     * @return Whether the recovery exception should be thrown every N invocations
     */
    public static boolean isShouldThrowRecoveryExceptionEveryNInvocations() {
        return shouldThrowRecoveryExceptionEveryNInvocations;
    }

    /**
     * Set whether to throw a recovery exception every N invocations.
     *
     * @param shouldThrowRecoveryExceptionEveryNInvocations true if a recovery exception should be thrown every N invocations, false otherwise
     */
    public static void setShouldThrowRecoveryExceptionEveryNInvocations(boolean shouldThrowRecoveryExceptionEveryNInvocations) {
        ExceptionToggle.shouldThrowRecoveryExceptionEveryNInvocations = shouldThrowRecoveryExceptionEveryNInvocations;
    }

    /**
     * Get the number of invocations before a retry should be attempted.
     *
     * @return The number of invocations before a retry should be attempted
     */
    public static int getNumberOfInvocationsBeforeRetry() {
        return numberOfInvocationsBeforeRetry;
    }

    /**
     * Set the number of invocations before retrying when encountering an exception.
     *
     * @param numberOfInvocationsBeforeRetry the number of invocations before retrying
     */
    public static void setNumberOfInvocationsBeforeRetry(int numberOfInvocationsBeforeRetry) {
        ExceptionToggle.numberOfInvocationsBeforeRetry = numberOfInvocationsBeforeRetry;
    }

    /**
     * This method retrieves the current value of the counter.
     *
     * @return The current value of the counter.
     */
    public static int getCounter() {
        return counter;
    }

    /**
     * Method to increment the counter value by 1.
     */
    public static void incrementCounter() {
        counter++;
    }

    /**
     * Retrieves the current count of recovery attempts.
     *
     * @return The current count of recovery attempts.
     */
    public static int getRecoveryCount() {
        return recoveryCount;
    }

    /**
     * Method to increment the recovery counter.
     */
    public static void incrementRecoveryCounter() {
        recoveryCount++;
    }

    /**
     * Resets the internal state of the ExceptionToggle class.
     * Sets all exception toggles to false, counters to zero, and recoveryCount to zero.
     * This method is synchronized to ensure thread safety when resetting the state.
     */
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
