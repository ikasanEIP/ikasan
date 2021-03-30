package org.ikasan.component.factory.spring;

import org.ikasan.spec.component.factory.ComponentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Used to create any ikasan component given a class, config prefix and factory config
 * prefix provided that ikasan component has defined a bean that implements the {@link ComponentFactory} interface
 */
@Component public class IkasanComponentFactory implements ApplicationContextAware
{
    private static Logger logger = LoggerFactory.getLogger(IkasanComponentFactory.class);

    private ApplicationContext applicationContext;



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
        String[] beanNamesForType = applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(ComponentFactory.class, clazz));

        for (String beanName : beanNamesForType)
        {
            ComponentFactory<T> componentFactory = (ComponentFactory<T>) applicationContext.getBean(beanName);
            candidates.add(componentFactory);
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


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
