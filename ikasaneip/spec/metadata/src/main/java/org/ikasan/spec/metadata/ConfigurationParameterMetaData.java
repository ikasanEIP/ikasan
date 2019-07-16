package org.ikasan.spec.metadata;

public interface ConfigurationParameterMetaData<T>
{
    /**
     * Get the configuration parameter nid if it has one
     * @return
     */
    Long getId();

    /**
     * Get the configuration parameter name
     * @return
     */
    String getName();


    /**
     * Get the configuration parameter value
     * @return
     */
    T getValue();

    /**
     * Get the configuration parameter description
     * @return
     */
    String getDescription();


    /**
     * Get the configuration parameter implementingClass
     * @return
     */
    String getImplementingClass();

}
