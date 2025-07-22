package ${moduleBasePackage};

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.module.Module;
import org.ikasan.spec.flow.Flow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class ModuleConfig
{
    @Value("${"$"}{module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
<#list flowModelMap?values as flow>
    @Resource
    private Flow ${flow.name?replace(" ", "")?uncap_first};
</#list>
    @Bean
    public Module getModule()
    {
        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        Module module = moduleBuilder.withDescription("todo")
<#list flowModelMap?values as flow>
            .addFlow(${flow.name?replace(" ", "")?uncap_first})
</#list>
            .build();

        return module;
    }
}
