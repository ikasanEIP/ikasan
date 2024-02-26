package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.migration.model.MigrationType;
import org.ikasan.cli.shell.migration.service.MigrationService;
import org.ikasan.cli.shell.operation.*;
import org.ikasan.cli.shell.operation.model.ProcessType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class H2DatabaseMigrationAggregateOperation extends AbstractAggregateOperation implements MigrationOperation {

    private String h2ScriptJavaCommand;
    private String h2RunScriptJavaCommand;
    private String h2ChangeLogRunScriptJavaCommand;
    private String sourceH2Version;
    private String targetH2Version;
    private String h2User;
    private String h2Password;
    private String databasePath;
    private String dbMigrationWorkingDirectory;
    private String migratedOutputSqlFileName;
    private String postProcessedOutputSqlFileName;

    private String persistenceDir;

    /**
     * Represents an aggregate operation for migrating H2 databases.
     */
    public H2DatabaseMigrationAggregateOperation(String h2ScriptJavaCommand, String h2RunScriptJavaCommand
        , String h2ChangeLogRunScriptJavaCommand , String sourceH2Version, String targetH2Version
        , String h2User, String h2Password, String databasePath, String dbMigrationWorkingDirectory
        , String migratedOutputSqlFileName, String postProcessedOutputSqlFileName, String persistenceDir) {
        this.h2ScriptJavaCommand = h2ScriptJavaCommand;
        if(this.h2ScriptJavaCommand == null) {
            throw new IllegalArgumentException("h2ScriptJavaCommand cannot be null!");
        }
        this.h2RunScriptJavaCommand = h2RunScriptJavaCommand;
        if (this.h2RunScriptJavaCommand == null) {
            throw new IllegalArgumentException("h2RunScriptJavaCommand cannot be null!");
        }
        this.h2ChangeLogRunScriptJavaCommand = h2ChangeLogRunScriptJavaCommand;
        if (this.h2ChangeLogRunScriptJavaCommand == null) {
            throw new IllegalArgumentException("h2ChangeLogRunScriptJavaCommand cannot be null!");
        }
        this.sourceH2Version = sourceH2Version;
        if (this.sourceH2Version == null) {
            throw new IllegalArgumentException("sourceH2Version cannot be null!");
        }
        this.targetH2Version = targetH2Version;
        if (this.targetH2Version == null) {
            throw new IllegalArgumentException("targetH2Version cannot be null!");
        }
        this.h2User = h2User;
        if (this.h2User == null) {
            throw new IllegalArgumentException("h2User cannot be null!");
        }
        this.h2Password = h2Password;
        if (this.h2Password == null) {
            throw new IllegalArgumentException("h2Password cannot be null!");
        }
        this.databasePath = databasePath;
        if (this.databasePath == null) {
            throw new IllegalArgumentException("databasePath cannot be null!");
        }
        this.dbMigrationWorkingDirectory = dbMigrationWorkingDirectory;
        if (this.dbMigrationWorkingDirectory == null) {
            throw new IllegalArgumentException("dbMigrationWorkingDirectory cannot be null!");
        }
        this.migratedOutputSqlFileName = migratedOutputSqlFileName;
        if (this.migratedOutputSqlFileName == null) {
            throw new IllegalArgumentException("migratedOutputSqlFileName cannot be null!");
        }
        this.postProcessedOutputSqlFileName = postProcessedOutputSqlFileName;
        if (this.postProcessedOutputSqlFileName == null) {
            throw new IllegalArgumentException("postProcessedOutputSqlFileName cannot be null!");
        }
        this.persistenceDir = persistenceDir;
        if (this.persistenceDir == null) {
            throw new IllegalArgumentException("persistenceDir cannot be null!");
        }
        super.operations = initialiseExecutableOperations();
    }

    @Override
    protected List<ExecutableOperation> initialiseExecutableOperations() {
        List<ExecutableOperation> executableOperations = new ArrayList<>();

        DefaultForkedExecutableOperationImpl dumpSourceToSql = new DefaultForkedExecutableOperationImpl(ProcessType.getH2Instance(),
            List.of(performTokenReplacements(this.h2ScriptJavaCommand)), "migrate-h2");
        executableOperations.add(dumpSourceToSql);

        H2DatabaseMigrationSourcePostProcessOperation h2DatabaseMigrationSourcePostProcess
            = new H2DatabaseMigrationSourcePostProcessOperation("./db-migration/migrated.sql"
            , "./db-migration/post-processed-migrated.sql");
        executableOperations.add(h2DatabaseMigrationSourcePostProcess);

        DefaultForkedExecutableOperationImpl migrateDataToNewTarget = new DefaultForkedExecutableOperationImpl(ProcessType.getH2Instance(),
            List.of(performTokenReplacements(this.h2RunScriptJavaCommand)), "migrate-h2");
        executableOperations.add(migrateDataToNewTarget);

        DefaultForkedExecutableOperationImpl updateChangeLogSql = new DefaultForkedExecutableOperationImpl(ProcessType.getH2Instance(),
            List.of(performTokenReplacements(this.h2ChangeLogRunScriptJavaCommand)), "migrate-h2");
        executableOperations.add(updateChangeLogSql);

        H2DatabaseMigrationSourceDatabaseFileRenameOperation h2DatabaseMigrationSourceDatabaseFileRenameOperation
            = new H2DatabaseMigrationSourceDatabaseFileRenameOperation(this.sourceH2Version, databasePath.substring(0, databasePath.lastIndexOf("/")), "esb");
        executableOperations.add(h2DatabaseMigrationSourceDatabaseFileRenameOperation);

        H2DatabaseMigrationTargetDatabaseFileRenameOperation h2DatabaseMigrationTargetDatabaseFileRenameOperation
            = new H2DatabaseMigrationTargetDatabaseFileRenameOperation(databasePath.substring(0, databasePath.lastIndexOf("/")), "esb");
        executableOperations.add(h2DatabaseMigrationTargetDatabaseFileRenameOperation);

        return executableOperations;
    }

    /**
     * Performs token replacements in the given command by replacing specific tokens
     * with their corresponding values.
     *
     * @param tokenizedCommand The command containing tokens to be replaced.
     * @return The command with token replacements performed.
     */
    private String performTokenReplacements(String tokenizedCommand) {
        String replaceResult = tokenizedCommand.replaceAll("\\[source.h2.version\\]", this.sourceH2Version)
            .replaceAll("\\[target.h2.version\\]", this.targetH2Version)
            .replaceAll("\\[database.username\\]", this.h2User)
            .replaceAll("\\[database.password\\]", this.h2Password)
            .replaceAll("\\[database.path\\]", this.databasePath);

        return replaceResult;
    }

    @Override
    public DefaultCheckMigrationRunOperationImpl getCheckMigrationRunOperation() {
        return new DefaultCheckMigrationRunOperationImpl(MigrationService.instance(this.persistenceDir)
            , MigrationType.H2_MIGRATION, this.sourceH2Version, this.targetH2Version);
    }

    @Override
    public DefaultMarkMigrationRunOperationImpl getMarkMigrationRunOperation() {
        return new DefaultMarkMigrationRunOperationImpl(MigrationService.instance(this.persistenceDir)
                , MigrationType.H2_MIGRATION, this.sourceH2Version, this.targetH2Version);
    }

    @Override
    public DefaultCleanTransientDirectoriesExecutableOperationImpl getCleanTransientDirectoriesExecutableOperation() {
        return new DefaultCleanTransientDirectoriesExecutableOperationImpl(List.of(new File(this.dbMigrationWorkingDirectory)));
    }
}
