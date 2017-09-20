package org.ikasan.builder;

import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleInitialisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * IM SpringBoot Developers can use this to easily create an Ikasan Spring boot application,
 * whilst having full control of the spring boot entry class and associated annotations
 */
@Component
public final class DefaultSpringBootIkasanApplication implements IkasanApplication, ApplicationContextAware{

    /**
     * DefaultSpringBootIkasanApplication Logger
     */
    private final static Logger logger = LoggerFactory.getLogger(DefaultSpringBootIkasanApplication.class);

    private ApplicationContext context;

    public static IkasanApplication createDefaultSpringBootIkasanApplication(Class<?> applicationClass, String[] args) {
        logger.debug("Spring boot createDefaultSpringBootIkasanApplication");
        final ConfigurableApplicationContext context = SpringApplication.run(applicationClass, args);
        final DefaultSpringBootIkasanApplication bean = context.getBean(DefaultSpringBootIkasanApplication.class);
        return bean;
    }


    public DefaultSpringBootIkasanApplication(){
        logger.debug("Default constructor called - provided for spring only");
    }

    public BuilderFactory getBuilderFactory()
    {
        return context.getBean(BuilderFactory.class);
    }

    public void run(Module module)
    {
        ModuleInitialisationService service = context.getBean(ModuleInitialisationService.class);
        service.register(module);
        logger.info("Module [" + module.getName() + "] successfully bootstrapped.");
    }

    // TODO - add close or shutdown per module ?
    public void close()
    {
        SpringApplication.exit(context, new ExitCodeGenerator(){
            @Override public int getExitCode()
            {
                return 0;
            }
        });
    }

    @Override
    public Module getModule(String moduleName)
    {
        ModuleContainer moduleContainer = context.getBean(ModuleContainer.class);
        return moduleContainer.getModule(moduleName);
    }

    @Override
    public List<Module> getModules()
    {
        ModuleContainer moduleContainer = context.getBean(ModuleContainer.class);
        return moduleContainer.getModules();
    }

    @Override
    public <COMPONENT> COMPONENT getBean(Class className)
    {
        return (COMPONENT) context.getBean(className);
    }

    @Override
    public <COMPONENT> COMPONENT getBean(String name, Class className)
    {
        return (COMPONENT) context.getBean(name, className);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }
}
