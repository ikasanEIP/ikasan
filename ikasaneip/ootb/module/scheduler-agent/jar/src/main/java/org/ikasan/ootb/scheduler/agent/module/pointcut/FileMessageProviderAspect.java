package org.ikasan.ootb.scheduler.agent.module.pointcut;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.ikasan.spec.configuration.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Aspect
public class FileMessageProviderAspect {

    private static Logger LOGGER = LoggerFactory.getLogger(FileMessageProviderAspect.class);

    // used for testing at the moment
    private boolean dryRunMode = false;

    @Autowired
    private ConfigurationService configurationService;

    @Around("execution(* org.ikasan.component.endpoint.filesystem.messageprovider.FileMessageProvider.invoke(..))")
    public Object fileMessageProviderInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        // todo get dry run mode from configuration
        if (dryRunMode) {
            // todo file in this section from configuration
            // System.out.println("joinPoint cutting ");
            LOGGER.info("In dry run mode joint point cutting FileMessageProvider.invoke");
            return List.of("some file name");
        } else {
            return joinPoint.proceed();
        }
    }
}
