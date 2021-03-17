package com.ikasan.component.factory;


import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.TargetSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Used to create any ikasan component given a class, config prefix and factory config
 * prefix
 */
@Component
public class IkasanComponentFactory {

    private static Logger logger = LoggerFactory.getLogger(IkasanComponentFactory.class);

    @Resource
    private JmsConsumerComponentFactory jmsConsumerComponentFactory;


    @Resource
    private JmsProducerComponentFactory jmsProducerComponentFactory;


    public <T> T create(String suffix, String prefix, String factoryConfigPrefix, Class<T> clazz) {
        if (JmsContainerConsumer.class.equals(clazz)) {
            return (T)(jmsConsumerComponentFactory.create(suffix, prefix, factoryConfigPrefix));
        }
        if (JmsTemplateProducer.class.equals(clazz)) {
            return (T)(jmsProducerComponentFactory.create(suffix, prefix, factoryConfigPrefix));
        }
        else {
            throw new RuntimeException("No factory found for class " + clazz.toString());
        }
    }

//    JmsContainerConsumer sampleJmsConsumer = ikasanComponentFactory.create("sampleJmsConsumer", "sample.jms", "sample.jms.consumer", JmsContainerConsumer.class);
//    JmsTemplateProducer jmsTemplateProducer = ikasanComponentFactory.create("sampleJmsProducer", "sample.jms", "sample.jms.producer", JmsTemplateProducer.class);

    public void populateAnnotations(Object instance){
        Field[] fields = ClassUtils.getUserClass(instance).getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(IkasanComponent.class)) {
                IkasanComponent ikasanComponent = field.getAnnotation(IkasanComponent.class);
                field.setAccessible(true);
                try {
                    String suffix = ikasanComponent.suffix().equals("") ? field.getName() : ikasanComponent.suffix();
                    String prefix = ikasanComponent.prefix();
                    String factoryPrefix = ikasanComponent.factoryPrefix().equals("")  ? prefix : ikasanComponent.factoryPrefix();
                    field.set(instance, this.create(suffix, prefix, factoryPrefix, field.getType()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Object getTargetObject(Object proxied) {
        String name = proxied.getClass().getName();
        if (name.toLowerCase().contains("cglib")) {
            return extractTargetObject(proxied);
        }
        return proxied;
    }

    private Object extractTargetObject(Object proxied) {
        try {
            return findSpringTargetSource(proxied).getTarget();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TargetSource findSpringTargetSource(Object proxied) {
        Method[] methods = proxied.getClass().getDeclaredMethods();
        Method targetSourceMethod = findTargetSourceMethod(methods,  proxied);
        targetSourceMethod.setAccessible(true);
        try {
            return (TargetSource)targetSourceMethod.invoke(proxied);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Method findTargetSourceMethod(Method[] methods, Object proxied) {
        for (Method method : methods) {
            if (method.getName().endsWith("getTargetSource")) {
                return method;
            }
        }
        throw new IllegalStateException(
            "Could not find target source method on proxied object ["
                + proxied.getClass() + "]");
    }
}
