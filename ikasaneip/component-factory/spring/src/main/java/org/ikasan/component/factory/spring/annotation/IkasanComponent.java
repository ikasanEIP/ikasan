package org.ikasan.component.factory.spring.annotation;



import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>An annotation used to mark a field and create the associated ikasan component provided it has a registered
 * {@link org.ikasan.spec.component.factory.ComponentFactory} bean using the {@link
 * org.ikasan.component.factory.spring.IkasanComponentFactory} bean. The annotations prefix,
 * factory and suffix are passed through to the
 * {@link org.ikasan.component.factory.spring.IkasanComponentFactory} bean along with the class of the annotated
 * field.</p><p></p><p></p>
 *
 * <p>The method {@link org.ikasan.component.factory.spring.IkasanComponentFactory#populateAnnotations(Object)} is used
 * to instantiate and set the annotated field</p>
 *
 * <p><p></p>Example Usage :-
 * </p>
 * <pre>
 * {@code
 *     @IkasanComponent(prefix="sample.jms", factoryPrefix = "sample.jms.consumer")
 *     private JmsContainerConsumer sampleJmsConsumer;
 *
 *     @IkasanComponent(prefix="sample.jms", factoryPrefix = "sample.jms.producer")
 *     private JmsTemplateProducer sampleJmsProducer;
 * }
 * </pre>
 * @see org.ikasan.component.factory.spring.IkasanComponentFactory
 * @see org.ikasan.spec.component.factory.ComponentFactory
 * @see org.ikasan.spec.configuration.ConfiguredResource
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IkasanComponent {
    /**
     * The prefix used to identify and load the configuration properties associated with the component
     * - can be empty if component isnt a {@link org.ikasan.spec.configuration.ConfiguredResource}
     * @return
     */
    String prefix() default "";

    /**
     * The factory prefix used to identify and load the configuration properties associated with the components
     * factory. May be empty.
     * @return
     */
    String factoryPrefix() default "";

    /**
     * The suffix of the component - this is added to the {@code ${module.name} } property to give the components name
     * @return
     */
    String suffix() default "";
}
