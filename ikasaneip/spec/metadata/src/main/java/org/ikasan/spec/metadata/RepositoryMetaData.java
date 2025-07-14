package org.ikasan.spec.metadata;

/**
 * Represents a Maven repository.
 */
public interface RepositoryMetaData {

    /**
     * Get the repository ID.
     * @return The repository ID.
     */
    String getId();

    /**
     * Set the repository ID.
     * @param id The repository ID.
     */
    void setId(String id);

    /**
     * Get the repository URL.
     * @return The repository URL.
     */
    String getUrl();

    /**
     * Set the repository URL.
     * @param url The repository URL.
     */
    void setUrl(String url);
}
