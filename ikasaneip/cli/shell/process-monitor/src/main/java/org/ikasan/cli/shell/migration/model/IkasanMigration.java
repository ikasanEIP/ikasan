package org.ikasan.cli.shell.migration.model;

import java.util.Objects;
import java.util.StringJoiner;

public class IkasanMigration {
    private String type;
    private String sourceVersion;
    private String targetVersion;
    private String label;
    private long migrationExecutionTimestamp;

    /**
     * Default constructor required by Kyro.
     */
    private IkasanMigration(){}

    /**
     * Creates a new instance of IkasanMigration.
     *
     * @param type The type of migration.
     * @param sourceVersion The source version of the migration.
     * @param targetVersion The target version of the migration.
     * @param label The label associated with the migrations.
     * @param migrationExecutionTimestamp The timestamp of the migration execution.
     * @throws IllegalArgumentException if any of the parameters are null.
     */
    public IkasanMigration(String type, String sourceVersion, String targetVersion
        , String label, long migrationExecutionTimestamp) {
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

    public String getLabel() {
        return label;
    }

    public long getMigrationExecutionTimestamp() {
        return migrationExecutionTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IkasanMigration that)) return false;
        return migrationExecutionTimestamp == that.migrationExecutionTimestamp
            && Objects.equals(type, that.type) && Objects.equals(sourceVersion, that.sourceVersion)
            && Objects.equals(targetVersion, that.targetVersion) && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, sourceVersion, targetVersion, label, migrationExecutionTimestamp);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IkasanMigration.class.getSimpleName() + "[", "]")
            .add("type='" + type + "'")
            .add("sourceVersion='" + sourceVersion + "'")
            .add("targetVersion='" + targetVersion + "'")
            .add("label='" + label + "'")
            .add("migrationExecutionTimestamp=" + migrationExecutionTimestamp)
            .toString();
    }
}
