package ${moduleBasePackage};

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.annotation.Value;

@Configuration
public class ModuleConfig
{
    @Value("${"$"}{module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
    @Resource
    private ComponentFactory componentFactory;

    @Bean
    public Module getModule()
    {
        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        Module module = moduleBuilder.withDescription("todo")
<#list flowModelMap?values as flow>
            .addFlow(${flow.name})
</#list>
            .build();

        return module;
    }
}
