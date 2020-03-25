package org.ikasan.dashboard.ui.visualisation.model.business.stream;

import org.ikasan.dashboard.ui.visualisation.correlate.Correlator;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.NodeFoundStatus;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

public class Flow extends Node
{
    private String state = FlowState.RUNNING;
    private String moduleName;
    private String flowName;
    private String wireapEvent;
    private Correlator correlator;

    public Flow(String id, String moduleName, String flowName, int x, int y)
    {
        super(id, ("\n").concat(moduleName).concat("\n").concat(flowName), Nodes.builder().withShape(Shape.image).withx(x)
            .withy(y).withImage("frontend/images/flow.png").withSize(20));
        this.moduleName = moduleName;
        this.flowName = flowName;
        super.setWiretapFoundStatus(NodeFoundStatus.EMPTY);
    }


    private void changeNodeStatusColour()
    {
        if(state == null)
        {
           return;
        }

        if(state.equals(FlowState.RUNNING))
        {
            super.setEdgeColour("rgba(0, 255, 0, 0.8)");
            super.setFillColour("rgba(0, 255, 0, 0.2)");
        }
        else if(state.equals(FlowState.STOPPED_IN_ERROR))
        {
            super.setEdgeColour("rgba(255, 0, 0, 0.8)");
            super.setFillColour("rgba(255, 0, 0, 0.2)");
        }
        else if(state.equals(FlowState.RECOVERING))
        {
            super.setEdgeColour("rgba(238, 108, 15, 0.8)");
            super.setFillColour("rgba(238, 108, 15, 0.2)");
        }
        else if(state.equals(FlowState.STOPPED))
        {
            super.setEdgeColour("rgba(0, 0, 255, 0.8)");
            super.setFillColour("rgba(0, 0, 255, 0.2)");
        }
        else if(state.equals(FlowState.PAUSED))
        {
            super.setEdgeColour("rgba(165, 26, 255, 0.8)");
            super.setFillColour("rgba(165, 26, 255, 0.2)");
        }
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getFlowName() {
        return flowName;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
        changeNodeStatusColour();
    }

    @Override
    public void setWiretapFoundStatus(String found)
    {
        super.setWiretapFoundStatus(found);
        if(found.equals(NodeFoundStatus.FOUND))
        {
            super.setWiretapFoundImage("frontend/images/wiretap-service.png");
        }
    }

    @Override
    public void setErrorFoundStatus(String errorFoundStatus) {
        super.setErrorFoundStatus(errorFoundStatus);

        if(errorFoundStatus.equals(NodeFoundStatus.FOUND))
        {
            super.setErrorFoundImage("frontend/images/error-service.png");
        }
    }

    @Override
    public void setExclusionFoundStatus(String exclusionFoundStatus) {
        super.setExclusionFoundStatus(exclusionFoundStatus);

        if(exclusionFoundStatus.equals(NodeFoundStatus.FOUND))
        {
            super.setExclusionFoundImage("frontend/images/hospital-service.png");
        }
    }

    @Override
    public void setReplayFoundStatus(String replayFoundStatus) {
        super.setReplayFoundStatus(replayFoundStatus);

        if(replayFoundStatus.equals(NodeFoundStatus.FOUND))
        {
            super.setReplayFoundImage("frontend/images/replay-service.png");
        }
    }

    public String getWireapEvent()
    {
        return wireapEvent;
    }

    public void setWireapEvent(String wireapEvent)
    {
        this.wireapEvent = wireapEvent;
    }

    public Correlator getCorrelator()
    {
        return correlator;
    }

    public void setCorrelator(Correlator correlator)
    {
        this.correlator = correlator;
    }
}
