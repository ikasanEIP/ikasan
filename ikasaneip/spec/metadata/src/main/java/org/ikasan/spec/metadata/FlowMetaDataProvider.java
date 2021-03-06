package org.ikasan.spec.metadata;

import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.StartupControl;

public interface FlowMetaDataProvider<T>
{
    /**
     * Method to convert a flow into a description of it.
     *
     * @param flow the flow we are describing.
     * @param startupType of flow we are describing.
     * @return the description of the flow.
     */
    public T describeFlow(Flow flow, StartupControl startupType);

    /**
     * Method to deserialise a meta data representation of a flow.
     *
     * @param flow the flow meta data
     * @return the deserialised flow meta data
     */
    public FlowMetaData deserialiseFlow(T flow);
}
