package ${moduleBasePackage};

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
import ${moduleBasePackage}.ComponentsAutoConfiguration;

@Configuration
@ImportResource( {
    <#list importedXmlResources as importedXmlResource>
        "${importedXmlResource.resource}"<#if importedXmlResource?has_next>, </#if>
    </#list>
} )
@Import({ ComponentsAutoConfiguration.class, <#list importedClassConfigurationResources as importedClassConfigurationResource>${importedClassConfigurationResource.resource}.class<#if importedClassConfigurationResource?has_next>, </#if></#list>})
public class ModuleConfig
{
    @Value("${"$"}{module.name}")
    private String moduleName;
    @Resource
    private BuilderFactory builderFactory;
<#list flowModelMap?values as flow>
    @Resource
    @Qualifier("${flow.name?replace(" ", "")?replace(",", "")?uncap_first}")
    private Flow ${flow.name?replace(" ", "")?replace(",", "")?uncap_first};
</#list>
    @Bean
    public Module getModule()
    {
        // get the builders
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder(moduleName);
        Module module = moduleBuilder.withDescription("todo")
<#list flowModelMap?values as flow>
            .addFlow(${flow.name?replace(" ", "")?replace(",", "")?uncap_first})
</#list>
            .build();

        return module;
    }
}
