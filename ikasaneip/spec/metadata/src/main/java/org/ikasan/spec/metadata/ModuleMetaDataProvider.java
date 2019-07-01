package org.ikasan.spec.metadata;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;

public interface ModuleMetaDataProvider<T>
{
    /**
     * Method to convert a module into a description of it.
     *
     * @param module module we are describing.
     * @return the description of the flow.
     */
    public T describeModule(Module<Flow> module);

    /**
     * Method to deserialise a meta data representation of a flow.
     *
     * @param module the module meta data
     * @return the deserialised module meta data
     */
    public ModuleMetaData deserialiseModule(T module);
}
