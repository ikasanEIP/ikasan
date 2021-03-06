package org.ikasan.spec.metadata;

public interface BusinessStreamMetaData<BUSINESS_STREAM>
{
    /**
     * Get the id.
     *
     * @return
     */
    public String getId();

    /**
     * Set the id.
     * @param id
     */
    public void setId(String id);

    /**
     * Get the business stream name.
     *
     * @return
     */
    public String getName();

    /**
     * Set the business stream name.
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Get the business description.
     *
     * @return
     */
    public String getDescription();

    /**
     * Set the business stream description.
     *
     * @param name
     */
    public void setDescription(String name);

    /**
     * Get the business stream json.
     *
     * @return
     */
    public String getJson();

    /**
     * Set the business stream json.
     *
     * @param json
     */
    public void setJson(String json);

    /**
     * Get the business stream.
     *
     * @return
     */
    public BUSINESS_STREAM getBusinessStream();
}
