package org.ikasan.spec.metadata.model;

public interface BusinessStreamMetaData<BUSINESS_STREAM>
{
    /**
     * Get the id.
     *
     * @return
     */
    String getId();

    /**
     * Set the id.
     * @param id
     */
    void setId(String id);

    /**
     * Get the business stream name.
     *
     * @return
     */
    String getName();

    /**
     * Set the business stream name.
     *
     * @param name
     */
    void setName(String name);

    /**
     * Get the business description.
     *
     * @return
     */
    String getDescription();

    /**
     * Set the business stream description.
     *
     * @param name
     */
    void setDescription(String name);

    /**
     * Get the business stream json.
     *
     * @return
     */
    String getJson();

    /**
     * Set the business stream json.
     *
     * @param json
     */
    void setJson(String json);

    /**
     * Get the business stream.
     *
     * @return
     */
    BUSINESS_STREAM getBusinessStream();
}
