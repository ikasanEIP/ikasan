package org.ikasan.spec.metadata;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.StartupControl;

import java.util.Map;

public interface ModuleMetaDataProvider<T>
{
    /**
     * Method to convert a module into a description of it.
     *
     * @param module module we are describing.
     * @param startUpControlMap map containing the startup controls
     * @return the description of the flow.
     */
    public T describeModule(Module<Flow> module, Map<String, StartupControl> startUpControlMap);

    /**
     * Method to deserialise a meta data representation of a flow.
     *
     * @param module the module meta data
     * @return the deserialised module meta data
     */
    public ModuleMetaData deserialiseModule(T module);
}
