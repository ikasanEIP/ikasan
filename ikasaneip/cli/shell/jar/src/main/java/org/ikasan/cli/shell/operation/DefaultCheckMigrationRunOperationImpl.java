package org.ikasan.cli.shell.operation;

import org.ikasan.cli.shell.migration.service.MigrationService;

public class DefaultCheckMigrationRunOperationImpl implements ExecutableOperation {
    protected MigrationService migrationService;
    protected String type;
    protected String sourceVersion;
    protected String targetVersion;
    protected String label;

    /**
     * CheckMigrationRunOperation class represents an executable operation that checks if a migration run
     * can be performed based on the given type, source version, and target version.
     */
    public DefaultCheckMigrationRunOperationImpl(MigrationService migrationService, String type
        , String sourceVersion, String targetVersion, String label) {
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
        this.label = label;
        if(this.label == null) {
            throw new IllegalArgumentException("label cannot be 'null'");
        }
    }

    @Override
    public String execute() throws RuntimeException {
        if(this.migrationService.find(this.type, this.sourceVersion, this.targetVersion, this.label) != null) {
            return MigrationOperation.RUN_PREVIOUSLY;
        }

        return MigrationOperation.NOT_YET_RUN;
    }

    @Override
    public String getCommand() {
        return this.getClass().getName();
    }
}
