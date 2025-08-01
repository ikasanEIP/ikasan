package com.ikasan.sample.spring.boot;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import com.ikasan.sample.spring.boot.ComponentsAutoConfiguration;

@Configuration
@ImportResource( {
    "classpath:ikasan-transaction-pointcut-jms.xml",
    "classpath:h2-datasource-conf.xml"
} )
@Import({ ComponentsAutoConfiguration.class})
public class ModuleConfig
{
    @Value("${module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    @Qualifier("jMSFLow")
    private Flow jMSFLow;
    @Resource
    @Qualifier("recipientListFLow")
    private Flow recipientListFLow;
    @Bean
    public Module getModule()
    {
        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        Module module = moduleBuilder.withDescription("todo")
            .addFlow(jMSFLow)
            .addFlow(recipientListFLow)
            .build();

        return module;
    }
}
