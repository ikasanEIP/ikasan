package com.ikasan.component.factory;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.Environment;

public class BindConfigurationHelper {

    public static <T> T createConfig(String prefix, Class<T> clazz, Environment env){
        Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.get(env);
        Binder binder = new Binder(sources, new PropertySourcesPlaceholdersResolver(env));
        return binder.bind(prefix, clazz).get();
    }
}
