package org.ikasan.component.factory.spring;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.Environment;

/**
 * Helper class used to bind configuration from spring .properties files, given a prefix
 */
public class BindConfigurationHelper
{

    public static <T> T createConfig(String prefix, Class<T> clazz, Environment env){
           Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(env);
            Binder binder = new Binder(sources, new PropertySourcesPlaceholdersResolver(env));
            return binder.bind(prefix, clazz).orElseGet(() -> { throw new
            IkasanComponentFactoryException("Unable to bind properties with prefix " + prefix + " to configuration "
            + " of type [{}] " + clazz.getSimpleName()
            + ". Please ensure you have defined properties for your component"); });

    }

}
