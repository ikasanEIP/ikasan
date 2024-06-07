package org.ikasan.cli.shell.operation;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class represents an abstract aggregate operation that executes a list of executable operations.
 */
public abstract class AbstractAggregateOperation {

    protected List<ExecutableOperation> operations;
    protected String fileNotFoundMessage = "The file to be migrated was not found!";

    /**
     * Initializes the list of executable operations.
     *
     * @return The list of executable operations.
     */
    protected abstract List<ExecutableOperation> initialiseExecutableOperations();

    /**
     * Sets the message to be displayed when a file is not found.
     *
     * @param fileNotFoundMessage the message to be displayed when a file is not found
     */
    protected void setFileNotFoundMessage(String fileNotFoundMessage) {
        this.fileNotFoundMessage = fileNotFoundMessage;
    };


    /**
     * Executes the aggregate operation by executing each executable operation in the list of operations.
     *
     * @return The result of the aggregate operation.
     *
     * @throws AggregateOperationException If an exception occurs while executing any of the operations.
     */
    public String execute() throws AggregateOperationException {
        if(this instanceof MigrationOperation) {
            ExecutableOperation checkMigrationRunOperation = ((MigrationOperation) this).getCheckMigrationRunOperation();
            String migrationRunCheck = checkMigrationRunOperation.execute();
            if(migrationRunCheck.equals(MigrationOperation.RUN_PREVIOUSLY)){
                return new JSONObject()
                    .put("result", String.format("The migration process has been run already and will not be re-run!"))
                    .toString();
            }
            else if(migrationRunCheck.equals(MigrationOperation.MIGRATION_FILE_NOT_FOUND)) {
                return new JSONObject()
                    .put("result", String.format(this.fileNotFoundMessage))
                    .toString();
            }
            else if(migrationRunCheck.equals(MigrationOperation.NOT_REQUIRED)) {
                return new JSONObject()
                    .put("result", String.format("The migration is not required."))
                    .toString();
            }
        }

        this.operations = initialiseExecutableOperations();

        AtomicReference<StringBuffer> result = new AtomicReference<>();
        result.set(new StringBuffer());

        this.operations.forEach(executableOperation -> {
            try {
                result.get().append(executableOperation.execute()).append("\r\n");
            }
            catch (Exception e) {
                throw new AggregateOperationException(String.format("An exception has occurred executing " +
                    "an executable operation!\r\nThe following opertation failed [%s]\r\n." +
                    "The following steps executed successfully:\r\n%s", executableOperation.getCommand(), result.get().toString()), e);
            }
        });

        if(this instanceof MigrationOperation) {
            if(((MigrationOperation) this).getMarkMigrationRunOperation() != null) {
                result.get().append(((MigrationOperation) this).getMarkMigrationRunOperation().execute()).append("\r\n");
            }
            if(((MigrationOperation) this).getCleanTransientDirectoriesExecutableOperation() != null) {
                result.get().append(((MigrationOperation) this).getCleanTransientDirectoriesExecutableOperation().execute()).append("\r\n");
            }
        }

        result.get().append("The aggregate operation has been migrated successfully!").append("\r\n");
        return new JSONObject()
            .put("result", result.get().toString())
            .toString();
    }
}
