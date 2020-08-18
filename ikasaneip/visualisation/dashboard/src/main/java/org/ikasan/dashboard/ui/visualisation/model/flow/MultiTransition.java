package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

import java.util.List;
import java.util.Map;

public interface MultiTransition
{
    public Map<String, Node> getTransitions();
}
