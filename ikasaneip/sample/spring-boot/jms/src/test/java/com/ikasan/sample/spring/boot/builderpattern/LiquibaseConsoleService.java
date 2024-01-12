package com.ikasan.sample.spring.boot.builderpattern;

import liquibase.Scope;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.ui.LoggerUIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;
import java.util.logging.Level;

public class LiquibaseConsoleService implements BeanPostProcessor {
    private static Logger logger = LoggerFactory.getLogger(LiquibaseConsoleService.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
        throws BeansException {
        if (bean instanceof SpringLiquibase) {
            try {
                Scope.enter(Map.of(Scope.Attr.ui.name(), new LoggerUIService()));
            } catch (Exception e) {
                logger.error("", e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (bean instanceof SpringLiquibase) {
//            try {
//                LoggerUIService loggerUIService = new LoggerUIService();
//                loggerUIService.setStandardLogLevel(Level.OFF);
//                Scope.enter(Map.of(Scope.Attr.ui.name(), loggerUIService));
//            } catch (Exception e) {
//                logger.error("", e);
//            }
//        }
        return bean;
    }
}
