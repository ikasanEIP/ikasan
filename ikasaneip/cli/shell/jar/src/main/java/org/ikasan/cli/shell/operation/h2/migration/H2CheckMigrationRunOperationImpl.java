package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.service.MigrationService;
import org.ikasan.cli.shell.operation.DefaultCheckMigrationRunOperationImpl;
import org.ikasan.cli.shell.operation.DefaultForkedExecutableOperationImpl;
import org.ikasan.cli.shell.operation.MigrationOperation;
import org.ikasan.cli.shell.operation.model.ProcessType;

import java.io.File;
import java.util.List;

public class H2CheckMigrationRunOperationImpl extends DefaultCheckMigrationRunOperationImpl {

    private String databaseLocation;
    private List<String> checkDbVersionCommands;


    /**
     * H2CheckMigrationRunOperationImpl represents an executable operation that checks if a migration run
     * can be performed on an H2 database.
     *
     * @param migrationService          the MigrationService instance used to interact with the persistence layer
     * @param type                      the type of migration
     * @param sourceVersion             the source version of the migration
     * @param targetVersion             the target version of the migration
     * @param databaseLocation          the location of the H2 database
     * @param checkDbVersionCommands    the list of commands to check the database version
     * @throws IllegalArgumentException if any of the parameters are null or empty
     */
    public H2CheckMigrationRunOperationImpl(MigrationService migrationService, String type
        , String sourceVersion, String targetVersion, String databaseLocation, String databaseName
        , List<String> checkDbVersionCommands) {
        super(migrationService, type, sourceVersion, targetVersion, databaseName);
        this.databaseLocation = databaseLocation;
        if(this.databaseLocation == null || this.databaseLocation.isEmpty()) {
            throw new IllegalArgumentException("databaseLocation cannot be null or empty!");
        }
        this.checkDbVersionCommands = checkDbVersionCommands;
        if(this.checkDbVersionCommands == null || this.checkDbVersionCommands.isEmpty()) {
            throw new IllegalArgumentException("checkDbVersionCommands cannot be null or empty!");
        }
    }

    @Override
    public String execute() throws RuntimeException {
        String result = super.execute();

        if(result.equals(MigrationOperation.RUN_PREVIOUSLY)) return result;

        if(!new File(this.databaseLocation+".mv.db").exists()) {
            IkasanMigration ikasanMigration = new IkasanMigration
                (type, sourceVersion, targetVersion, this.databaseLocation.substring(this.databaseLocation.lastIndexOf("/")+1)
                    , System.currentTimeMillis());
            this.migrationService.save(ikasanMigration);
            return MigrationOperation.MIGRATION_FILE_NOT_FOUND;
        }
        else {
            DefaultForkedExecutableOperationImpl testDbNoAlreadyOnLatestVersion = new DefaultForkedExecutableOperationImpl(ProcessType.getH2Instance(),
                this.checkDbVersionCommands, "check-h2");

            try {
                testDbNoAlreadyOnLatestVersion.execute();
                IkasanMigration ikasanMigration = new IkasanMigration
                    (type, sourceVersion, targetVersion, this.databaseLocation.substring(this.databaseLocation.lastIndexOf("/")+1)
                        , System.currentTimeMillis());
                this.migrationService.save(ikasanMigration);
                return MigrationOperation.NOT_REQUIRED;
            }
            catch (Exception e) {
                // we can ignore this exception as it indicates that the migration has not yet run.
            }
        }

        return result;
    }
}
