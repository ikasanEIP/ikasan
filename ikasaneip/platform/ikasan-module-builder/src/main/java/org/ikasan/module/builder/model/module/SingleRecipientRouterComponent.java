package org.ikasan.module.builder.model.module;

public class SingleRecipientRouterComponent extends AbstractMultiTransition {
    public static final String TYPE = "org.ikasan.spec.component.router.SingleRecipientRouterComponent";



    /**
     * Constructs a SingleRecipientRouterComponent with the specified name and implementing class.
     * The SingleRecipientRouterComponent extends AbstractMultiTransition and is used to route messages to a single recipient.
     *
     * @param name the name of the SingleRecipientRouterComponent
     * @param implementingClass the implementing class of the SingleRecipientRouterComponent
     */
    public SingleRecipientRouterComponent(String name, String implementingClass) {
        super(name, TYPE, implementingClass);
    }
}
