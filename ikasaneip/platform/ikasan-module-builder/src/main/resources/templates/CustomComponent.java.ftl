package ${packageName};

import ${interfaceType};
<#if isConfigured>
import org.ikasan.spec.component.ConfiguredResource;
import org.ikasan.spec.configuration.Configuration;
import ${configurationPackageName}.${configurationClassName};
</#if>

public class ${simpleClassName} implements ${interfaceType} <#if isConfigured> , ConfiguredResource<${configurationClassName}> </#if> {

    <#if isConfigured>
    private String configuredResourceId;
    private ${configurationClassName} configuration;
    </#if>

    @Override
    public ${invokeMethodSignature} {
        // TODO: Implement custom logic for ${simpleClassName} invoke method
        // Note: The invoke method signature is based on the component type.
        //       You may need to adjust it based on the specific Ikasan interface.
        System.out.println("Invoking ${simpleClassName}");
        return null;
    }

<#if isConfigured>
    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public ${configurationClassName} getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(${configurationClassName} configuration) {
        this.configuration = configuration;
        // TODO: Implement configuration logic for ${simpleClassName}
        System.out.println("Setting configuration for ${simpleClassName}");
    }
</#if>
}