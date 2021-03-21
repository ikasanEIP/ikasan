package org.ikasan.component.factory.spring;

import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.factory.spring.annotation.IkasanComponent;
import org.ikasan.component.factory.spring.endpoint.JmsConsumerComponentFactory;
import org.ikasan.component.factory.spring.endpoint.JmsProducerComponentFactory;
import org.ikasan.spec.component.factory.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Used to create any ikasan component given a class, config prefix and factory config
 * prefix provided that ikasan component has defined a bean that implements the {@link ComponentFactory} interface
 */
@Component public class IkasanComponentFactory
{
    private static Logger logger = LoggerFactory.getLogger(IkasanComponentFactory.class);

    /**
     * All the component factory beans in the application
     */
    @Autowired private List<ComponentFactory> componentFactories;

    /**
     * This is used to create any component that has an associated {@link ComponentFactory} e.g.
     * <pre>
     * {@code
     *     JmsContainerConsumer sampleJmsConsumer = ikasanComponentFactory.create("sampleJmsConsumer",
     *     "sample.jms", "sample.jms.consumer", JmsContainerConsumer.class);
     *     JmsTemplateProducer jmsTemplateProducer = ikasanComponentFactory.create("sampleJmsProducer",
     *     "sample.jms", "sample.jms.producer", JmsTemplateProducer.class);
     * }
     * </pre>
     * Will throw a {@link IkasanComponentFactoryException} exception if
     * <ul>
     *     <li>There is more than one {@link ComponentFactory} bean that creates components of the requested type</li>
     *     <li>There are no {@link ComponentFactory} beans that create components of the requested type</li>
     * </ul>
     *
     * @param suffix              Used with module name to identify the component
     * @param prefix              The prefix used to identify and load the configuration properties associated with the component
     * @param factoryConfigPrefix The prefix to load any factory configuration properties
     * @param clazz               The class of the desired component
     * @param <T>                 The type of the component that the found {@link ComponentFactory#create(String,
     * String, String)} method returns
     * @return An instance of the desired component
     * @see ComponentFactory
     */
    public <T> T create(String suffix, String prefix, String factoryConfigPrefix, Class<T> clazz)
    {
        ComponentFactory<T> componentFactory = getComponentFactory(clazz);
        return componentFactory.create(suffix, prefix, factoryConfigPrefix);
    }

    /**
     * Uses the {@link GenericTypeResolver#resolveTypeArgument(Class, Class)} to evaluate the generic type used by
     * the components {@link ComponentFactory} and check that matches the passed in class value
     *
     * @param clazz the clazz of component that the component factory instantiates
     * @param <T>   the type parameter
     * @return
     */
    private <T> ComponentFactory<T> getComponentFactory(Class<T> clazz)
    {
        Set<ComponentFactory<T>> candidates = new LinkedHashSet<>();
        for (ComponentFactory componentFactory : componentFactories)
        {
            Class<?> componentClass = GenericTypeResolver
                .resolveTypeArgument(componentFactory.getClass(), ComponentFactory.class);
            if (componentClass.equals(clazz))
            {
                candidates.add(componentFactory);
            }
        }
        if (candidates.size() > 1)
        {
            logger.warn("Detected the following candidate factories " + candidates + " should only be one for class " +
                clazz.getName());
            throw new IkasanComponentFactoryException(
                "Found " + candidates.size()  + " candidate factories for component class " + clazz.getName()
                    + " please ensure there is only one defined");
        }
        if (candidates.size() == 0)
        {
            throw new IkasanComponentFactoryException("Found no component factory for component class " + clazz.getName());
        }
        return candidates.iterator().next();
    }


}
