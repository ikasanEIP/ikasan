package org.ikasan.dashboard.ui.visualisation.event;

import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;

public class GraphViewChangeEvent
{
    private Module module;
    private Flow flow;

    public GraphViewChangeEvent(Module module, Flow flow)
    {
        this.module = module;
        this.flow = flow;
    }

    public Module getModule()
    {
        return module;
    }

    public Flow getFlow()
    {
        return flow;
    }
}
