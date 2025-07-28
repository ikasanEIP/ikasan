package org.ikasan.module.builder.model.module;

import java.util.HashMap;
import java.util.Map;

public class AbstractMultiTransition extends Component implements MultiTransition {
    protected Map<String, Component> transitions;

    public AbstractMultiTransition(String name, String componentType, String implementingClass) {
        super(name, componentType, implementingClass);
        this.transitions = new HashMap<>();
    }

    @Override
    public Map<String, Component> getTransitions() {
        return this.transitions;
    }

    public void addTransition(String context, Component component)
    {
        this.transitions.put(context, component);
    }
}
