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
    private String determineIfDbFileAlreadyTargetVersionCommand;
    private String sourceH2Version;
    private String targetH2Version;
    private String h2User;
    private String h2Password;
    private String databasePath;
    private String dbMigrationWorkingDirectory;
    private String migratedOutputSqlFileName;
    private String postProcessedOutputSqlFileName;
    private String persistenceDir;
    private boolean isEsbDatabase;
    private List<String> previouslySupportedMigrationTargetVersions;

    private long forkedProcessTimeout;


    /**
     * Constructs an H2DatabaseMigrationAggregateOperation instance to handle migration operations
     * for H2 databases across different versions. Initializes all necessary commands, configurations,
     * user credentials, directory paths, and process settings required for migration.
     *
     * @param h2ScriptJavaCommand The command to run the H2 script for dumping data from the source database.
     * @param h2RunScriptJavaCommand The command to run the H2 script for applying data migration to the target database.
     * @param h2ChangeLogRunScriptJavaCommand The command to update the changelog during migration, if necessary.
     * @param determineIfDbFileAlreadyTargetVersionCommand The command to check if the database file is already at the target version.
     * @param sourceH2Version The source version of the H2 database from which the migration begins.
     * @param targetH2Version The target version of the H2 database to which the migration occurs.
     * @param h2User The username for connecting to the H2 database.
     * @param h2Password The password for the specified H2 user.
     * @param databasePath The path to the H2 database files.
     * @param dbMigrationWorkingDirectory The working directory to store intermediate files during migration.
     * @param migratedOutputSqlFileName The file name for the SQL dump from the source database.
     * @param postProcessedOutputSqlFileName The file name for the post-processed SQL dump.
     * @param persistenceDir The directory where persistent migration files are stored.
     * @param isEsbDatabase A boolean flag indicating whether the database is an ESB database.
     * @param forkedProcessTimeout The timeout value for forked processes during the migration.
     * @param previouslySupportedMigrationTargetVersions A list of previously supported migration target versions for compatibility.
     * @throws IllegalArgumentException If any of the required parameters is null.
     */
    public H2DatabaseMigrationAggregateOperation(String h2ScriptJavaCommand, String h2RunScriptJavaCommand
        , String h2ChangeLogRunScriptJavaCommand, String determineIfDbFileAlreadyTargetVersionCommand
        , String sourceH2Version, String targetH2Version
        , String h2User, String h2Password, String databasePath, String dbMigrationWorkingDirectory
        , String migratedOutputSqlFileName, String postProcessedOutputSqlFileName, String persistenceDir
        , boolean isEsbDatabase, long forkedProcessTimeout, List<String> previouslySupportedMigrationTargetVersions) {
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
        this.determineIfDbFileAlreadyTargetVersionCommand = determineIfDbFileAlreadyTargetVersionCommand;
        if (this.determineIfDbFileAlreadyTargetVersionCommand == null) {
            throw new IllegalArgumentException("determineIfDbFileAlreadyTargetVersionCommand cannot be null!");
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
        this.previouslySupportedMigrationTargetVersions = previouslySupportedMigrationTargetVersions;
        if (this.previouslySupportedMigrationTargetVersions == null) {
            throw new IllegalArgumentException("previouslySupportedMigrationTargetVersions cannot be null!");
        }
        this.isEsbDatabase = isEsbDatabase;
        this.forkedProcessTimeout = forkedProcessTimeout;
        super.operations = initialiseExecutableOperations();
    }

    @Override
    protected List<ExecutableOperation> initialiseExecutableOperations() {
        List<ExecutableOperation> executableOperations = new ArrayList<>();

        DefaultForkedExecutableOperationImpl dumpSourceToSql = new DefaultForkedExecutableOperationImpl(ProcessType.getH2Instance(),
            List.of(performTokenReplacements(this.h2ScriptJavaCommand)), "migrate-h2", this.forkedProcessTimeout);
        executableOperations.add(dumpSourceToSql);

        H2DatabaseMigrationSourcePostProcessOperation h2DatabaseMigrationSourcePostProcess
            = new H2DatabaseMigrationSourcePostProcessOperation("./db-migration/migrated.sql"
            , "./db-migration/post-processed-migrated.sql");
        executableOperations.add(h2DatabaseMigrationSourcePostProcess);

        DefaultForkedExecutableOperationImpl migrateDataToNewTarget = new DefaultForkedExecutableOperationImpl(ProcessType.getH2Instance(),
            List.of(performTokenReplacements(this.h2RunScriptJavaCommand)), "migrate-h2", this.forkedProcessTimeout);
        executableOperations.add(migrateDataToNewTarget);

        if(this.isEsbDatabase) {
            DefaultForkedExecutableOperationImpl updateChangeLogSql = new DefaultForkedExecutableOperationImpl(ProcessType.getH2Instance(),
                List.of(performTokenReplacements(this.h2ChangeLogRunScriptJavaCommand)), "migrate-h2", this.forkedProcessTimeout);
            executableOperations.add(updateChangeLogSql);
        }

        H2DatabaseMigrationSourceDatabaseFileRenameOperation h2DatabaseMigrationSourceDatabaseFileRenameOperation
            = new H2DatabaseMigrationSourceDatabaseFileRenameOperation(this.sourceH2Version, databasePath.substring(0, databasePath.lastIndexOf("/"))
                , databasePath.substring(databasePath.lastIndexOf("/")+1));
        executableOperations.add(h2DatabaseMigrationSourceDatabaseFileRenameOperation);

        H2DatabaseMigrationTargetDatabaseFileRenameOperation h2DatabaseMigrationTargetDatabaseFileRenameOperation
            = new H2DatabaseMigrationTargetDatabaseFileRenameOperation(databasePath.substring(0, databasePath.lastIndexOf("/"))
                , databasePath.substring(databasePath.lastIndexOf("/")+1));
        executableOperations.add(h2DatabaseMigrationTargetDatabaseFileRenameOperation);

        super.setFileNotFoundMessage(String.format("Database file[%s] was not found so there is nothing to migrate. A new empty database will be" +
            " created when the module is next started.", this.databasePath + ".mv.db"));
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
        return new H2CheckMigrationRunOperationImpl(MigrationService.instance(this.persistenceDir)
            , MigrationType.H2_MIGRATION, this.sourceH2Version, this.targetH2Version, this.databasePath
            , databasePath.substring(databasePath.lastIndexOf("/")+1)
            , List.of(this.performTokenReplacements(this.determineIfDbFileAlreadyTargetVersionCommand))
            , this.forkedProcessTimeout, this.previouslySupportedMigrationTargetVersions);
    }

    @Override
    public DefaultMarkMigrationRunOperationImpl getMarkMigrationRunOperation() {
        return new DefaultMarkMigrationRunOperationImpl(MigrationService.instance(this.persistenceDir)
                , MigrationType.H2_MIGRATION, this.sourceH2Version, this.targetH2Version
                , this.databasePath.substring(this.databasePath.lastIndexOf("/")+1));
    }

    @Override
    public DefaultCleanTransientDirectoriesExecutableOperationImpl getCleanTransientDirectoriesExecutableOperation() {
        return new DefaultCleanTransientDirectoriesExecutableOperationImpl(List.of(new File(this.dbMigrationWorkingDirectory)));
    }
}
