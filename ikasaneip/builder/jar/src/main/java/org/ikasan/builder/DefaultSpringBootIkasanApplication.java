package org.ikasan.builder;

import org.ikasan.spec.module.Module;
import org.ikasan.spec.module.ModuleContainer;
import org.ikasan.spec.module.ModuleInitialisationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * IM SpringBoot Developers can use this to easily create an Ikasan Spring boot application,
 * whilst having full control of the spring boot entry class and associated annotations
 */
public class DefaultSpringBootIkasanApplication implements IkasanApplication{

    /**
     * DefaultSpringBootIkasanApplication Logger
     */
    private final static Logger logger = LoggerFactory.getLogger(DefaultSpringBootIkasanApplication.class);

    private ApplicationContext context;

    protected DefaultSpringBootIkasanApplication(Class<?> applicationClass, String[] args) {
        logger.debug("Spring boot constructor called");
        context = SpringApplication.run(applicationClass,args);}

    /**
     * Default constructor
     *
     * This should never be called by client code, its required by Spring.
     *
     * TODO - Investigate why spring boot is calling this - this instance should never get used,
     * TODO - there is no need to create two instances of this class, this needs to be prevented.
     */
    public DefaultSpringBootIkasanApplication(){
        logger.debug("Default constructor called - provided for spring only");
    }

    public BuilderFactory getBuilderFactory()
    {
        return context().getBean(BuilderFactory.class);
    }

    public void run(Module module)
    {
        ModuleInitialisationService service = context().getBean(ModuleInitialisationService.class);
        service.register(module);
        logger.info("Module [" + module.getName() + "] successfully bootstrapped.");
    }

    // TODO - add close or shutdown per module ?
    public void close()
    {
        SpringApplication.exit(context(), new ExitCodeGenerator(){
            @Override public int getExitCode()
            {
                return 0;
            }
        });
    }

    @Override
    public Module getModule(String moduleName)
    {
        ModuleContainer moduleContainer = context().getBean(ModuleContainer.class);
        return moduleContainer.getModule(moduleName);
    }

    @Override
    public List<Module> getModules()
    {
        ModuleContainer moduleContainer = context().getBean(ModuleContainer.class);
        return moduleContainer.getModules();
    }

    @Override
    public <COMPONENT> COMPONENT getBean(Class className)
    {
        return (COMPONENT) context().getBean(className);
    }

    @Override
    public <COMPONENT> COMPONENT getBean(String name, Class className)
    {
        return (COMPONENT) context().getBean(name, className);
    }

    public ApplicationContext context() {
        if (context == null){
            throw new RuntimeException("Context is null - this is because you have used the default constructor, " +
                    "this is required by spring. Please ensure you only use the spring boot constructor specifying " +
                    "your spring boot class and arguments.");
        } else {
            return context;
        }
    }
}
