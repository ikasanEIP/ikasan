package org.ikasan.spec.metadata;

import java.util.List;

public interface ScheduledConsumerMetaData {
    /**
     * Sets the name of the object.
     *
     * @param name the new name to set
     */
    void setName(String name);

    /**
     * Returns the name associated with this object.
     *
     * @return the name of the object as a String
     */
    String getName();

    /**
     * Sets the flow for the system.
     *
     * @param flow the flow to be set for the system
     */
    void setFlow(String flow);

    /**
     * Retrieves the flow information.
     *
     * @return The flow information as a String.
     */
    String getFlow();

    /**
     * Sets the class name of the message provider to be used.
     *
     * @param messageProviderClass the name of the message provider class as a String
     */
    void setMessageProviderClass(String messageProviderClass);

    /**
     * Retrieves the class name of the message provider to be used.
     *
     * @return the class name of the message provider as a String
     */
    String getMessageProviderClass();

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
