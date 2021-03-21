package org.ikasan.spec.component.factory;

/**
 * Used to instantiate components in a consistent manner
 *
 * T - The type of the component being created
 */
public interface ComponentFactory<T>
{
    /**
     * Creates a component
     *
     * @param nameSuffix The name of the component to be added to the module name
     * @param configPrefix If the component has configuration this is the prefix used to lookup the associated
     *                    configuration
     * @param factoryConfigPrefix If the component has factory configuration this is the prefix used to look up
     *                            the associated configuration
     * @return the created component of type T
     */
    T create (String nameSuffix, String configPrefix, String factoryConfigPrefix);
}
