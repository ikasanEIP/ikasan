package org.ikasan.cli.shell.operation;

public interface MigrationOperation {
    String NOT_YET_RUN = "NOT_YET_RUN";
    String RUN_PREVIOUSLY = "RUN_PREVIOUSLY";

    /**
     * Retrieves a {@link DefaultCheckMigrationRunOperationImpl} object.
     *
     * @return the {@link DefaultCheckMigrationRunOperationImpl} object
     */
     ExecutableOperation getCheckMigrationRunOperation();


     /**
      * Retrieves a {@link DefaultMarkMigrationRunOperationImpl} object.
      *
      * @return the {@link DefaultMarkMigrationRunOperationImpl} object
      */
     ExecutableOperation getMarkMigrationRunOperation();


     /**
      * Cleans transient working directories.
      */
     ExecutableOperation getCleanTransientDirectoriesExecutableOperation();

}
