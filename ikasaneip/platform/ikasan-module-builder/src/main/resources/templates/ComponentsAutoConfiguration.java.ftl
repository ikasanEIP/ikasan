package ${packageName};

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.ConfigurationProperties;
<#list components as component>
    import ${component.componentType};
    import ${component.implementingClass};
</#list>
<#list componentConfigurations as componentConfiguration>
    import ${componentConfiguration.packageName}.${componentConfiguration.className};
</#list>
@Configuration
public class ComponentsAutoConfiguration {
<#list components as component>
    /**
    * Create the ${component.name?replace(" ", "")?uncap_first} bean.
    *
    * @return the ${component.name?replace(" ", "")?uncap_first} bean.
    */
    @Bean("${component.name?replace(" ", "")?uncap_first}")
    public ${component.componentTypeClassName} ${component.name?replace(" ", "")?uncap_first}() {
        return new ${component.className}();
    }

</#list>

<#list componentConfigurations as componentConfiguration>
    /**
    * Create the ${componentConfiguration.className?replace(" ", "")?uncap_first} bean.
    *
    * @return the ${componentConfiguration.className?replace(" ", "")?uncap_first} bean.
    */
    @Bean("${componentConfiguration.className?replace(" ", "")?uncap_first}")
    @ConfigurationProperties(prefix = "${componentConfiguration.className?replace(" ", "")?uncap_first}")
    public ${componentConfiguration.className?replace(" ", "")} ${componentConfiguration.className?replace(" ", "")?uncap_first}() {
    return new ${componentConfiguration.className}();
    }

</#list>
}
