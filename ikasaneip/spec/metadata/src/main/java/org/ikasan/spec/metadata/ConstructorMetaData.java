package org.ikasan.spec.metadata;

import java.util.List;

public interface ConstructorMetaData {

    /**
     * Retrieves the name of the component.
     *
     * @return the name of the component as a String.
     */
    String getComponentName();

    /**
     * Sets the name of the component to the provided value.
     *
     * @param ComponentName the new name to be assigned to the component
     */
    void setComponentName(String ComponentName);

    /**
     * Retrieves the class name associated with this ConstructorMetaData.
     *
     * @return the class name as a String
     */
    String getClassName();

    /**
     * Sets the class name for this ConstructorMetaData object.
     *
     * @param className the new class name to be set
     */
    void setClassName(String className);

    /**
     * Retrieves the list of TypeParameters that represent the constructor arguments associated with this ConstructorMetaData.
     *
     * @return the list of TypeParameters representing the constructor arguments
     */
    List<TypeParameter> getConstructorArguments();

    /**
     * Sets the list of TypeParameters representing the constructor arguments for this ConstructorMetaData object.
     *
     * @param constructorArguments the list of TypeParameters representing the constructor arguments to be set
     */
    void setConstructorArguments(List<TypeParameter> constructorArguments);
}
