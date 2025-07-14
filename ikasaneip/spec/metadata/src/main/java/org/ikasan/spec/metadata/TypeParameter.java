package org.ikasan.spec.metadata;

public interface TypeParameter {

    /**
     * Returns the name of the TypeParameter.
     *
     * @return the name of the TypeParameter as a String
     */
    String getName();

    /**
     * Sets the name of the TypeParameter.
     *
     * @param name the new name to set for the TypeParameter
     */
    void setName(String name);

    /**
     * Get the type of the TypeParameter.
     *
     * @return the type of the TypeParameter as a String
     */
    String getType();

    /**
     * Sets the type of the TypeParameter.
     *
     * @param type the new type of the TypeParameter
     */
    void setType(String type);
}
