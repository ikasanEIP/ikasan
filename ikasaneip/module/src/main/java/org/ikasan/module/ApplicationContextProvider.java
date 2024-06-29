package org.ikasan.module;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public class ApplicationContextProvider {

    private ApplicationContext applicationContext;
    private static ApplicationContextProvider instance;

    private ApplicationContextProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static void init(ApplicationContext applicationContext) throws BeansException {
        if(instance == null) {
            instance = new ApplicationContextProvider(applicationContext);
        }
    }

    public static ApplicationContextProvider instance() {
        return instance;
    }

    public ApplicationContext getContext() {
        return applicationContext;
    }
    
}