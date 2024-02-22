package org.ikasan.cli.shell.operation;

import org.ikasan.cli.shell.migration.service.MigrationService;

public class CheckMigrationRunOperation implements ExecutableOperation {
    private MigrationService migrationService;
    private String type;
    private String sourceVersion;
    private String targetVersion;

    /**
     * CheckMigrationRunOperation class represents an executable operation that checks if a migration run
     * can be performed based on the given type, source version, and target version.
     */
    public CheckMigrationRunOperation(MigrationService migrationService, String type
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
        if(this.migrationService.find(this.type, this.sourceVersion, this.targetVersion) != null) {
            return MigrationOperation.RUN_PREVIOUSLY;
        }

        return MigrationOperation.NOT_YET_RUN;
    }
}
