package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;

import java.util.HashMap;
import java.util.Map;

public class AbstractMultiTransition extends AbstractWiretapNode implements MultiTransition {
    protected Map<String, Node> transitions;

    public AbstractMultiTransition(String id, String name, String image) {
        super(id, name, image);
        this.transitions = new HashMap<>();
    }

    @Override
    public Map<String, Node> getTransitions() {
        return this.transitions;
    }
}
