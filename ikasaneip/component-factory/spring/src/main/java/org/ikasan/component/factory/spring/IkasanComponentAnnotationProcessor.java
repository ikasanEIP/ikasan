package org.ikasan.component.factory.spring;

import org.ikasan.component.factory.spring.annotation.IkasanComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * Will post process all beans looking for fields annotated with the IkasanComponent annotation
 *
 */
public class IkasanComponentAnnotationProcessor implements BeanPostProcessor
{
    private static Logger logger = LoggerFactory.getLogger(IkasanComponentAnnotationProcessor.class);

    @Resource
    private IkasanComponentFactory ikasanComponentFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        populateAnnotations(bean);
        return bean;
    }

    /**
     * Given an instance checks for {@link IkasanComponent} annotations and creates the component by calling the
     * {@link IkasanComponentFactory#create(String, String, String, Class)} method
     *
     * @param instance The instance to set the created component on
     */
    public void populateAnnotations(Object instance)
    {
        Field[] fields = ClassUtils.getUserClass(instance).getDeclaredFields();
        for (Field field : fields)
        {
            if (field.isAnnotationPresent(IkasanComponent.class))
            {
                IkasanComponent ikasanComponent = field.getAnnotation(IkasanComponent.class);
                field.setAccessible(true);
                try
                {
                    String suffix = ikasanComponent.suffix().equals("") ? field.getName() : ikasanComponent.suffix();
                    String prefix = ikasanComponent.prefix();
                    String factoryPrefix = ikasanComponent.factoryPrefix().equals("") ?
                        prefix :
                        ikasanComponent.factoryPrefix();
                    logger.info("Creating @IkasanComponent with suffix [{}], prefix [{}], factoryPrefix [{}] "
                        + "of type [{}]. ", suffix,prefix,factoryPrefix,field.getType().getSimpleName());
                    field.set(instance, ikasanComponentFactory.create(suffix, prefix, factoryPrefix, field.getType()));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
