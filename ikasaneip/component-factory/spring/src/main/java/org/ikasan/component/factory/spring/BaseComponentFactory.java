package org.ikasan.component.factory.spring;

import liquibase.pro.packaged.C;
import org.ikasan.spec.component.factory.ComponentFactory;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * For simple components this will look up both the component and factory configuration and set the configured
 * resource id correctly.
 *
 * @param <T>
 */
@Component public abstract class BaseComponentFactory<T> implements ComponentFactory<T>
{
    @Autowired protected Environment env;

    @Value("${module.name}") private String moduleName;

    protected <C> C configuration(String configPrefix, Class<C> clazz)
    {
        return BindConfigurationHelper.createConfig(configPrefix, clazz, env);
    }

    protected <F> F factoryConfiguration(String factoryConfigPrex, Class<F> clazz)
    {
        return BindConfigurationHelper.createConfig(factoryConfigPrex, clazz, env);
    }

    protected String configuredResourceId(String nameSuffix)
    {
        return moduleName + "-" + nameSuffix;
    }


}
