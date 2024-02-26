package org.ikasan.cli.shell.operation;

import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.ikasan.cli.shell.migration.service.MigrationService;

public class DefaultMarkMigrationRunOperationImpl implements ExecutableOperation {

    private MigrationService migrationService;
    private String type;
    private String sourceVersion;
    private String targetVersion;

    /**
     * MarkMigrationRunOperation class represents an executable operation that marks a migration as run
     * based on the given type, source version, and target version.
     */
    public DefaultMarkMigrationRunOperationImpl(MigrationService migrationService, String type
        , String sourceVersion, String targetVersion) {
        this.migrationService = migrationService;
        if(this.migrationService == null) {
            throw new IllegalArgumentException("migrationService cannot be 'null'");
        }
        this.type = type;
        if(this.type == null) {
            throw new IllegalArgumentException("type cannot be 'null'");
        }
        this.sourceVersion = sourceVersion;
        if(this.sourceVersion == null) {
            throw new IllegalArgumentException("sourceVersion cannot be 'null'");
        }
        this.targetVersion = targetVersion;
        if(this.targetVersion == null) {
            throw new IllegalArgumentException("targetVersion cannot be 'null'");
        }
    }

    @Override
    public String execute() throws RuntimeException {
        try {
            IkasanMigration ikasanMigration = new IkasanMigration(type, sourceVersion, targetVersion, System.currentTimeMillis());
            this.migrationService.save(ikasanMigration);
            return String.format("The following migration has been marked as executed: %s", ikasanMigration);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to IkasanMigration", e);
        }
    }
}
