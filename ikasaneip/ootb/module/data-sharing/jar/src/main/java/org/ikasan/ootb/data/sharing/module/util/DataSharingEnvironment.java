package org.ikasan.ootb.data.sharing.module.util;

import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import java.util.HashMap;

public class DataSharingEnvironment implements Environment {

    private HashMap<String, String> properties = new HashMap<>();

    @Override
    public String[] getActiveProfiles() {
        throw new NotImplementedException();
    }

    @Override
    public String[] getDefaultProfiles() {
        throw new NotImplementedException();
    }

    @Override
    public boolean acceptsProfiles(String... profiles) {
        throw new NotImplementedException();
    }

    @Override
    public boolean acceptsProfiles(Profiles profiles) {
        throw new NotImplementedException();
    }

    @Override
    public boolean containsProperty(String key) {
        throw new NotImplementedException();
    }

    @Override
    public @Nullable String getProperty(String key) {
        return properties.get(key);
    }

    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        if(!properties.containsKey(key)) return defaultValue;
        return properties.get(key);
    }

    @Override
    public @Nullable <T> T getProperty(String key, Class<T> targetType) {
        throw new NotImplementedException();
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        throw new NotImplementedException();
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        throw new NotImplementedException();
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        throw new NotImplementedException();
    }

    @Override
    public String resolvePlaceholders(String text) {
        throw new NotImplementedException();
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        throw new NotImplementedException();
    }
}
