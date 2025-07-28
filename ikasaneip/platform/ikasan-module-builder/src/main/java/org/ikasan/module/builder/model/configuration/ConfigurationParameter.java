package org.ikasan.module.builder.model.configuration;

public class ConfigurationParameter {
    private String name;
    private String type;
    private String description;
    private String fullyQualifiedType;

    /**
     * Retrieves the name of this configuration parameter.
     *
     * @return The name of the configuration parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the configuration parameter.
     *
     * @param name The name to set for the configuration parameter
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the type of the configuration parameter.
     *
     * @return the type of the configuration parameter as a String
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the configuration parameter.
     *
     * @param type the new type to be set for the configuration parameter
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retrieves the description of this configuration parameter.
     *
     * @return The description of the configuration parameter.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for the configuration parameter.
     *
     * @param description The description to set for the configuration parameter.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the fully qualified type of a ConfigurationParameter.
     *
     * @return A string representing the fully qualified type of the ConfigurationParameter.
     */
    public String getFullyQualifiedType() {
        return fullyQualifiedType;
    }

    /**
     * Sets the fully-qualified type of a configuration parameter.
     *
     * @param fullyQualifiedType the fully-qualified type to be set
     */
    public void setFullyQualifiedType(String fullyQualifiedType) {
        this.fullyQualifiedType = fullyQualifiedType;
    }
}
