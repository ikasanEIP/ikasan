package org.ikasan.cli.shell.migration.model;

import java.util.Objects;
import java.util.StringJoiner;

public class IkasanMigration {
    private String type;
    private String sourceVersion;
    private String targetVersion;
    private long migrationExecutionTimestamp;

    /**
     * Default constructor required by Kyro.
     */
    private IkasanMigration(){}

    public IkasanMigration(String type, String sourceVersion, String targetVersion, long migrationExecutionTimestamp) {
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
        this.migrationExecutionTimestamp = migrationExecutionTimestamp;
    }

    public String getType() {
        return type;
    }

    public String getSourceVersion() {
        return sourceVersion;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public long getMigrationExecutionTimestamp() {
        return migrationExecutionTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IkasanMigration that)) return false;
        return migrationExecutionTimestamp == that.migrationExecutionTimestamp && Objects.equals(type, that.type) && Objects.equals(sourceVersion, that.sourceVersion) && Objects.equals(targetVersion, that.targetVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, sourceVersion, targetVersion, migrationExecutionTimestamp);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IkasanMigration.class.getSimpleName() + "[", "]")
            .add("type='" + type + "'")
            .add("sourceVersion='" + sourceVersion + "'")
            .add("targetVersion='" + targetVersion + "'")
            .add("migrationExecutionTimestamp=" + migrationExecutionTimestamp)
            .toString();
    }
}
