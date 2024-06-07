package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.operation.ExecutableOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class H2DatabaseMigrationSourceDatabaseFileRenameOperation implements ExecutableOperation {

    private static Logger logger = LoggerFactory.getLogger(H2DatabaseMigrationSourceDatabaseFileRenameOperation.class);

    private String persistenceDirectory;
    private String sourceDatabaseName;
    private String sourceVersion;

    public H2DatabaseMigrationSourceDatabaseFileRenameOperation(String sourceVersion
        , String persistenceDirectory, String sourceDatabaseName) {
        this.sourceVersion = sourceVersion;
        if (this.sourceVersion == null) {
            throw new IllegalArgumentException("sourceVersion cannot be null!");
        }
        this.persistenceDirectory = persistenceDirectory;
        if (this.persistenceDirectory == null) {
            throw new IllegalArgumentException("persistenceDirectory cannot be null!");
        }
        this.sourceDatabaseName = sourceDatabaseName;
        if (this.sourceDatabaseName == null) {
            throw new IllegalArgumentException("sourceDatabaseName cannot be null!");
        }
    }

    @Override
    public String execute() throws RuntimeException {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY-hhmmss");
        String backupName = persistenceDirectory + "/" + sourceDatabaseName + ".mv.db-backup-"+sourceVersion
            +"-"+dateFormat.format(new Date());

        try {
            Files.move(Paths.get(persistenceDirectory + "/" + sourceDatabaseName + ".mv.db")
                , Paths.get(backupName), StandardCopyOption.REPLACE_EXISTING);

            return String.format("Successfully backed up source database from [%s] to [%s]",
                persistenceDirectory + "/" + sourceDatabaseName + ".mv.db", backupName);
        }
        catch (IOException e) {
            throw new RuntimeException(String.format("Could not rename the source database file from [%s] to [%s]!",
                persistenceDirectory + "/" + sourceDatabaseName + ".mv.db", backupName), e);
        }
    }

    @Override
    public String getCommand() {
        return String.format("Backing up file[%s]" ,persistenceDirectory + "/" + sourceDatabaseName + ".mv.db");
    }
}
