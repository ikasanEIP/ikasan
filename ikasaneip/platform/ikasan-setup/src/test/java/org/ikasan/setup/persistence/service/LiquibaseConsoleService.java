package org.ikasan.setup.persistence.service;

import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.ui.LoggerUIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

public class LiquibaseConsoleService implements BeanPostProcessor {
    private static Logger logger = LoggerFactory.getLogger(LiquibaseConsoleService.class);

//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName)
//        throws BeansException {
//        if (bean instanceof Liquibase) {
//            try {
//                Scope.enter(Map.of(Scope.Attr.ui.name(), new LoggerUIService()));
//            } catch (Exception e) {
//                logger.error("", e);
//            }
//        }
//        return bean;
//    }
}
