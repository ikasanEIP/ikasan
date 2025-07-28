package org.ikasan.module.builder.model.component;

import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConstructorMetaData;
import org.ikasan.spec.metadata.ParameterizedType;

import java.util.List;

public class Component {
    private String name;
    private String implementingClass;
    private String className;
    private String classPackage;
    private String componentType;
    private String componentTypeClassName;
    private String componentTypePackage;
    private ParameterizedType parameterizedType;
    private boolean isConfigured;
    private ComponentConfigurationMetaData configurationMetaData;
    private List<ConstructorMetaData> constructorMetaData;

    /**
     * Retrieves the name of the component.
     *
     * @return A String representing the name of the component.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the Component.
     *
     * @param name The name to set for the Component.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the implementing class name associated with this Component.
     *
     * @return A String representing the implementing class name of the Component.
     */
    public String getImplementingClass() {
        return implementingClass;
    }

    /**
     * Sets the implementing class for the component.
     *
     * @param implementingClass A String representing the implementing class to be set for the component.
     */
    public void setImplementingClass(String implementingClass) {
        this.implementingClass = implementingClass;
    }

    /**
     * Retrieves the class name of the component.
     *
     * @return The class name of the component.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name for the Component.
     *
     * @param className the name of the class to be set for the Component
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Retrieves the package name of the class.
     *
     * @return The package name of the class.
     */
    public String getClassPackage() {
        return classPackage;
    }

    /**
     * Sets the package name for the class.
     *
     * @param classPackage A String representing the package name to be set for the class.
     */
    public void setClassPackage(String classPackage) {
        this.classPackage = classPackage;
    }

    /**
     * Retrieves the type of the component.
     *
     * @return the type of the component as a String
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * Sets the component type for the Component object.
     *
     * @param componentType A String representing the type of the component.
     */
    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    /**
     * Retrieves the class name of the component type.
     *
     * @return The class name of the component type as a String.
     */
    public String getComponentTypeClassName() {
        return componentTypeClassName;
    }

    /**
     * Sets the class name for the component type of this Component object.
     *
     * @param componentTypeClassName A String representing the class name of the component type to be set.
     */
    public void setComponentTypeClassName(String componentTypeClassName) {
        this.componentTypeClassName = componentTypeClassName;
    }

    /**
     * Retrieves the package name of the component type.
     *
     * @return A String representing the package name of the component type.
     */
    public String getComponentTypePackage() {
        return componentTypePackage;
    }

    /**
     * Sets the package name for the component type.
     *
     * @param componentTypePackage A String representing the package name to set for the component type.
     */
    public void setComponentTypePackage(String componentTypePackage) {
        this.componentTypePackage = componentTypePackage;
    }

    /**
     * Retrieves the parameterized type associated with this Component.
     *
     * @return the ParameterizedType representing the parameterized type of this Component
     */
    public ParameterizedType getParameterizedType() {
        return parameterizedType;
    }

    /**
     * Sets the parameterized type for the component.
     *
     * @param parameterizedType the parameterized type to set for the component
     */
    public void setParameterizedType(ParameterizedType parameterizedType) {
        this.parameterizedType = parameterizedType;
    }

    /**
     * Retrieves the boolean value indicating if the component is configured.
     *
     * @return true if the component is configured, false otherwise
     */
    public boolean getIsConfigured() {
        return isConfigured;
    }

    /**
     * Sets the flag indicating if the component is configured.
     *
     * @param configured true if the component is configured, false otherwise
     */
    public void setConfigured(boolean configured) {
        isConfigured = configured;
    }

    /**
     * Retrieves the ComponentConfigurationMetaData associated with this Component.
     *
     * @return the ComponentConfigurationMetaData object containing configuration metadata for this Component.
     */
    public ComponentConfigurationMetaData getConfigurationMetaData() {
        return configurationMetaData;
    }

    /**
     * Sets the configuration meta data for the component.
     *
     * @param configurationMetaData The configuration meta data to be set for the component.
     */
    public void setConfigurationMetaData(ComponentConfigurationMetaData configurationMetaData) {
        this.configurationMetaData = configurationMetaData;
    }

    /**
     * Get the ConstructorMetaData associated with this Component.
     *
     * @return the list of ConstructorMetaData representing the constructors of this Component
     */
    public List<ConstructorMetaData> getConstructorMetaData() {
        return constructorMetaData;
    }

    /**
     * Sets the ConstructorMetaData list for this Component.
     *
     * @param constructorMetaData The list of ConstructorMetaData objects to be set for this Component.
     */
    public void setConstructorMetaData(List<ConstructorMetaData> constructorMetaData) {
        this.constructorMetaData = constructorMetaData;
    }
}
