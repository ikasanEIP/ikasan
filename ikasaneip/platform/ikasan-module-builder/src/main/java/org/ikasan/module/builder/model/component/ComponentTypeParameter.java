package org.ikasan.module.builder.model.component;

import org.ikasan.manifest.model.TypeParameterImpl;

public class ComponentTypeParameter extends TypeParameterImpl {
    private String parameterClass;

    /**
     * Returns the class of the parameter as a String.
     *
     * @return the class of the parameter
     */
    public String getParameterClass() {
        return parameterClass;
    }

    /**
     * Set the parameter class for the current component type.
     *
     * @param parameterClass The class name to set as the parameter class
     */
    public void setParameterClass(String parameterClass) {
        this.parameterClass = parameterClass;
    }
}
