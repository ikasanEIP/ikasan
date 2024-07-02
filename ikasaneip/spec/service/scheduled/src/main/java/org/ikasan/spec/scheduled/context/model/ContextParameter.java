package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface ContextParameter extends Serializable {

    /**
     * Retrieves the name of the object.
     *
     * @return The name of the object as a String.
     */
    String getName();

    /**
     * Sets the name of the object.
     *
     * @param name the new name to set
     */
    void setName(String name);

    /**
     * Retrieves the default value of the context parameter.
     *
     * @return The default value of the context parameter.
     */
    String getDefaultValue();

    /**
     * Sets the default value for the context parameter.
     *
     * @param defaultValue The default value for the context parameter
     */
    void setDefaultValue(String defaultValue);
}
