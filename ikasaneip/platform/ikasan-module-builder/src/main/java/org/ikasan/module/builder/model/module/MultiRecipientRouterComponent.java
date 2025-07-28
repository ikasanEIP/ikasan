package org.ikasan.module.builder.model.module;

public class MultiRecipientRouterComponent extends AbstractMultiTransition {
    public static final String TYPE = "org.ikasan.spec.component.router.MultiRecipientRouterComponent";


    /**
     * Represents a multi-recipient router component that routes messages to multiple recipients based on certain criteria.
     *
     * @param name the name of the multi-recipient router
     * @param implementingClass the implementing class of the multi-recipient router
     */
    public MultiRecipientRouterComponent(String name, String implementingClass) {
        super(name, TYPE, implementingClass);
    }
}
