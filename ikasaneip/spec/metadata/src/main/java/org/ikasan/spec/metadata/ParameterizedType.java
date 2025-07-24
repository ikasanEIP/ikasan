package org.ikasan.spec.metadata;

import java.util.List;

public interface ParameterizedType {

    /**
     * Retrieves the full class name of the implementing class.
     *
     * @return A String representing the fully qualified class name of the implementing class.
     */
    String getImplementingClassName();

    /**
     * Sets the name of the class implementing the interface represented by this object.
     *
     * @param implementingClassName the name of the class implementing the interface
     */
    void setImplementingClassName(String implementingClassName);


    /**
     * Retrieves the list of TypeParameter objects representing the parameterized types of this object.
     *
     * @return A List of TypeParameter objects representing the parameterized types of this object.
     */
    List<TypeParameter> getTypeParameters();


    /**
     * Sets the list of parameterized types for the implementing class.
     *
     * @param typeParameters the list of TypeParameters to set as parameterized types
     */
    void setTypeParameters(List<TypeParameter> typeParameters);
}
