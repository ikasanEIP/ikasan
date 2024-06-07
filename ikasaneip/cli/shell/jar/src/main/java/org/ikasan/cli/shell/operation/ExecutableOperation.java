package org.ikasan.cli.shell.operation;

/**
 * Represents an operation that can be executed.
 */
public interface ExecutableOperation {

    /**
     * Method to execute a java operation.
     *
     * @throws RuntimeException
     */
    String execute() throws RuntimeException;


    /**
     * Returns the command associated with the ExecutableOperation.
     *
     * @return the command associated with the ExecutableOperation
     */
    String getCommand();
}
