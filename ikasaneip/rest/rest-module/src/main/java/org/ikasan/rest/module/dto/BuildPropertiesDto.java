package org.ikasan.rest.module.dto;

import java.time.Instant;

public class BuildPropertiesDto {
    private String group;
    private String artifact;
    private String name;
    private String version;
    private long buildTimestamp;
    private String ikasanVersion;

    /**
     * Retrieves the group of the BuildPropertiesDto object.
     *
     * @return The group of the BuildPropertiesDto object.
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the group of the BuildPropertiesDto object.
     *
     * @param group the group to set
     */
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Retrieves the artifact of the BuildPropertiesDto.
     *
     * @return the artifact of the BuildPropertiesDto
     */
    public String getArtifact() {
        return artifact;
    }

    /**
     * Sets the artifact of the BuildPropertiesDto.
     *
     * @param artifact the artifact to be set
     */
    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    /**
     * Retrieves the name of the object.
     *
     * @return The name of the object.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the object.
     *
     * @param name the new name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the version of the build.
     *
     * @return the version of the build
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version of the build properties.
     *
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Retrieves the build timestamp of the BuildPropertiesDto object.
     *
     * @return the build timestamp
     */
    public long getBuildTimestamp() {
        return buildTimestamp;
    }

    /**
     * Sets the build timestamp in the BuildPropertiesDto object.
     *
     * @param buildTimestamp The build timestamp value to be set.
     */
    public void setBuildTimestamp(long buildTimestamp) {
        this.buildTimestamp = buildTimestamp;
    }

    /**
     * Retrieves the version of Ikasan.
     *
     * @return the Ikasan version as a string
     */
    public String getIkasanVersion() {
        return ikasanVersion;
    }

    /**
     * Sets the Ikasan version for this BuildPropertiesDto instance.
     *
     * @param ikasanVersion the Ikasan version to set
     */
    public void setIkasanVersion(String ikasanVersion) {
        this.ikasanVersion = ikasanVersion;
    }
}
