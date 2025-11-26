package org.ikasan.module.builder.model.component;

import org.ikasan.spec.metadata.ConstructorMetaData;
import org.ikasan.spec.metadata.ParameterizedType;

import java.util.List;
import java.util.Objects;

public class BeanComponent {
    private String name;
    private String implementingClass;
    private String className;
    private String classPackage;
    private boolean isLocal;
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
     * Retrieves the boolean value indicating if the component is local.
     *
     * @return true if the component is local, false otherwise
     */
    public boolean getIsLocal() {
        return isLocal;
    }

    /**
     * Sets the flag indicating if the component is local.
     *
     * @param local A boolean value indicating if the component is local.
     */
    public void setLocal(boolean local) {
        isLocal = local;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanComponent component = (BeanComponent) o;
        return isLocal == component.isLocal
            && Objects.equals(name, component.name)
            && Objects.equals(implementingClass, component.implementingClass)
            && Objects.equals(className, component.className)
            && Objects.equals(classPackage, component.classPackage)
            && Objects.equals(constructorMetaData, component.constructorMetaData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, implementingClass, className, classPackage, isLocal

            , constructorMetaData);
    }
}
