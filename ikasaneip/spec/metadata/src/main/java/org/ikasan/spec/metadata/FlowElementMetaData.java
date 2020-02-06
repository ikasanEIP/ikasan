package org.ikasan.spec.metadata;

import java.util.List;

public interface FlowElementMetaData
{
    /**
     * Accessor for the componentName. This is the unique identifier for a <code>FlowElement</code>
     *
     * @return componentName
     */
    String getComponentName();

    /**
     * Set the component name.
     *
     * @param componentName
     */
    void setComponentName(String componentName);

    /**
     * Returns a human readable description of this FlowElement
     *
     * @return String description
     */
    String getDescription();

    /**
     * Set the description.
     *
     * @param description
     */
    void setDescription(String description);

    /**
     * Get the component type associated with the flow element.
     *
     * @return the component type
     */
    String getComponentType();

    /**
     * Set the component type.
     *
     * @param componentType
     */
    void setComponentType(String componentType);

    /**
     * Get the implementing class.
     *
     * @return
     */
    String getImplementingClass();

    /**
     * Set the implementing class.
     *
     * @param implementingClass
     */
    void setImplementingClass(String implementingClass);

    /**
     * Is the component configurable.
     *
     * @return
     */
    boolean isConfigurable();

    /**
     * Set whether the component is configurable.
     *
     * @param configurable
     */
    void setConfigurable(boolean configurable);

    /**
     * Get the configuration id of the component.
     *
     * @return
     */
    String getConfigurationId();

    /**
     * Set the component configuration id.
     *
     * @param configurationId
     */
    void setConfigurationId(String configurationId);

    /**
     * Get the configuration id of the component.
     *
     * @return
     */
    String getInvokerConfigurationId();

    /**
     * Set the component configuration id.
     *
     * @param configurationId
     */
    void setInvokerConfigurationId(String configurationId);


    /**
     * Get the decorators.
     *
     * @return
     */
    List<DecoratorMetaData> getDecorators();

    /**
     * Set the transitions.
     *
     * @param decorators
     */
    void setDecorators(List<DecoratorMetaData> decorators);


}
