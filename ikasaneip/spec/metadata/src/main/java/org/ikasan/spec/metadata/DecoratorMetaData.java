package org.ikasan.spec.metadata;

public interface DecoratorMetaData
{
    /**
     * Accessor for the ame. This is the unique identifier for a this decorator.
     *
     * @return componentName
     */
    public String getName();

    /**
     * Set the  name.
     *
     * @param name
     */
    public void setName(String name);

    /**
     * Get the type associated with this decorator.
     *
     * @return the component type
     */
    public String getType();

    /**
     * Set the type.
     *
     * @param type
     */
    public void setType(String type);

    /**
     * Is the component configurable.
     *
     * @return
     */
    public boolean isConfigurable();

    /**
     * Set whether the component is configurable.
     *
     * @param configurable
     */
    public void setConfigurable(boolean configurable);

    /**
     * Get the configuration id of the component.
     *
     * @return
     */
    public String getConfigurationId();

    /**
     * Set the component configuration id.
     *
     * @param configurationId
     */
    public void setConfigurationId(String configurationId);


}
