package org.ikasan.spec.metadata;

import java.util.Map;

public interface FlowElementMetaData
{
    /**
     * Accessor for the componentName. This is the unique identifier for a <code>FlowElement</code>
     *
     * @return componentName
     */
    public String getComponentName();

    /**
     * Set the component name.
     *
     * @param componentName
     */
    public void setComponentName(String componentName);

    /**
     * Returns a human readable description of this FlowElement
     *
     * @return String description
     */
    public String getDescription();

    /**
     * Set the description.
     *
     * @param description
     */
    public void setDescription(String description);

    /**
     * Get the component type associated with the flow element.
     *
     * @return the component type
     */
    public String getComponentType();

    /**
     * Set the component type.
     *
     * @param componentType
     */
    public void setComponentType(String componentType);

    /**
     * Get the implementing class.
     *
     * @return
     */
    public String getImplementingClass();

    /**
     * Set the implementing class.
     *
     * @param implementingClass
     */
    public void setImplementingClass(String implementingClass);

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

    /**
     * Get the configuration id of the component.
     *
     * @return
     */
    public String getInvokerConfigurationId();

    /**
     * Set the component configuration id.
     *
     * @param configurationId
     */
    public void setInvokerConfigurationId(String configurationId);


}
