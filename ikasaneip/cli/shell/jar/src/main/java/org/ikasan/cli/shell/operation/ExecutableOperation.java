package org.ikasan.cli.shell.operation;

/**
 * Represents an executable operation that can be executed.
 */
public interface ExecutableOperation {

    /**
     * Method to execute a java operation.
     *
     * @throws RuntimeException
     */
    String execute() throws RuntimeException;
}