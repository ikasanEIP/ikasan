package org.ikasan.spec.metadata;

public interface BeanDefinitionMetaData {

    /**
     * Retrieves the name of the bean represented by this metadata.
     *
     * @return the name of the bean
     */
    String getBeanName();

    /**
     * Set the name of the bean associated with this metadata.
     *
     * @param beanName the name of the bean
     */
    void setBeanName(String beanName);

    /**
     * Retrieves the type of the bean definition.
     *
     * @return The type of the bean definition as a String.
     */
    String getType();

    /**
     * Set the type of the bean definition.
     *
     * @param type the type of the bean definition
     */
    void setType(String type);

    /**
     * Retrieves the class name of the bean.
     *
     * @return the class name of the bean
     */
    String getBeanClass();

    /**
     * Sets the class name for the bean defined in this metadata.
     *
     * @param beanClass the fully qualified class name of the bean
     */
    void setBeanClass(String beanClass);

    /**
     * Returns the resource path of the bean definition.
     *
     * @return the resource path of the bean definition as a String
     */
    String getBeanResource();

    /**
     * Set the resource for the bean definition.
     *
     * @param beanResource the resource location for the bean definition
     */
    void setBeanResource(String beanResource);
}
