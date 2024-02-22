package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.operation.ExecutableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class H2DatabaseMigrationTargetDatabaseFileRenameOperation implements ExecutableOperation {

    private String persistenceDirectory;
    private String targetDatabaseName;

    public H2DatabaseMigrationTargetDatabaseFileRenameOperation(String persistenceDirectory, String targetDatabaseName) {
        this.persistenceDirectory = persistenceDirectory;
        if (this.persistenceDirectory == null) {
            throw new IllegalArgumentException("persistenceDirectory cannot be null!");
        }
        this.targetDatabaseName = targetDatabaseName;
        if (this.targetDatabaseName == null) {
            throw new IllegalArgumentException("targetDatabaseName cannot be null!");
        }
    }

    @Override
    public String execute() throws RuntimeException {
        try {
            Files.move(Paths.get(persistenceDirectory + "/" + targetDatabaseName + "-new.mv.db")
                , Paths.get(persistenceDirectory + "/" + targetDatabaseName + ".mv.db")
                , StandardCopyOption.REPLACE_EXISTING);

            return String.format("Successfully renamed newly created target database to the original database name " +
                    "from [%s] to [%s]",
                persistenceDirectory + "/" + targetDatabaseName + "-new.mv.db",
                persistenceDirectory + "/" + targetDatabaseName + ".mv.db");
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("Could not rename the target database file from [%s] to [%s]!",
                persistenceDirectory + "/" + targetDatabaseName + "-new.mv.db", persistenceDirectory + "/"
                    + targetDatabaseName + ".mv.db"), e);
        }
    }
}
