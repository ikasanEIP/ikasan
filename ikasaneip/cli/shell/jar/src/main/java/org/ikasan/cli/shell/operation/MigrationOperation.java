package org.ikasan.cli.shell.operation;

public interface MigrationOperation {
    String NOT_YET_RUN = "NOT_YET_RUN";
    String RUN_PREVIOUSLY = "RUN_PREVIOUSLY";

    /**
     * Retrieves a {@link CheckMigrationRunOperation} object.
     *
     * @return the {@link CheckMigrationRunOperation} object
     */
     CheckMigrationRunOperation getCheckMigrationRunOperation();


     /**
      * Retrieves a {@link MarkMigrationRunOperation} object.
      *
      * @return the {@link MarkMigrationRunOperation} object
      */
     MarkMigrationRunOperation getMarkMigrationRunOperation();


     /**
      * Cleans transient working directories.
      */
     CleanTransientDirectoriesExecutableOperation getCleanTransientDirectoriesExecutableOperation();

}
