package org.ikasan.spec.metadata;

public interface ImportedResourceMetaData {
    public static final String IMPORTED_XML_RESOURCE = "IMPORTED_XML_RESOURCE";
    public static final String IMPORTED_CONFIGURATION_CLASS = "IMPORTED_CONFIGURATION_CLASS";

    /**
     * Set the source of the imported resource.
     *
     * @param source The source of the imported resource to be set
     */
    void setSource(String source);

    /**
     * Get the source of the imported resource.
     *
     * @return The source of the imported resource.
     */
    String getSource();

    /**
     * Set the type of the resource.
     *
     * @param resourceType The type of the resource to be set.
     */
    void setResourceType(String resourceType);

    /**
     * Get the type of the resource.
     *
     * @return The type of the resource.
     */
    String getResourceType();

    /**
     * Sets the resource for this imported object.
     *
     * @param resource The resource to be set for this object.
     */
    void setResource(String resource);

    /**
     * This method returns the resource associated with this object.
     *
     * @return The resource associated with this object.
     */
    String getResource();
}
