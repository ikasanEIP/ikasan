package org.ikasan.cli.shell.operation.h2.migration;

import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.service.MigrationService;
import org.ikasan.cli.shell.operation.DefaultCheckMigrationRunOperationImpl;
import org.ikasan.cli.shell.operation.MigrationOperation;

import java.io.File;

public class H2CheckMigrationRunOperationImpl extends DefaultCheckMigrationRunOperationImpl {

    private String databaseLocation;

    /**
     * CheckMigrationRunOperation class represents an executable operation that checks if a migration run
     * can be performed based on the given type, source version, and target version.
     *
     * @param migrationService
     * @param type
     * @param sourceVersion
     * @param targetVersion
     */
    public H2CheckMigrationRunOperationImpl(MigrationService migrationService, String type
        , String sourceVersion, String targetVersion, String databaseLocation) {
        super(migrationService, type, sourceVersion, targetVersion);
        this.databaseLocation = databaseLocation;
        if(this.databaseLocation == null || this.databaseLocation.isEmpty()) {
            throw new IllegalArgumentException("databaseLocation cannot be null or empty!");
        }
    }

    @Override
    public String execute() throws RuntimeException {
        if(!new File(this.databaseLocation).exists()) {
            IkasanMigration ikasanMigration = new IkasanMigration(type, sourceVersion, targetVersion, System.currentTimeMillis());
            this.migrationService.save(ikasanMigration);
            return MigrationOperation.NOT_REQUIRED;
        }
        return super.execute();
    }
}
