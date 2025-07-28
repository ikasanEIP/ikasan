package ${packageName};

<#list configurationParameters as configurationParameter>
    import ${configurationParameter.fullyQualifiedType};
</#list>

public class ${className} {
<#list configurationParameters as configurationParameter>
    private ${configurationParameter.type} ${configurationParameter.name};
</#list>

<#list configurationParameters as configurationParameter>
    /**
    * Set the ${configurationParameter.name} configuration value.
    *
    * @param ${configurationParameter.name} the configuration value to set.
    */
    public ${configurationParameter.type} get${configurationParameter.name?cap_first}() {
        return ${configurationParameter.name};
    }

    /**
    * Get the ${configurationParameter.name} configuration value.
    *
    * @return ${configurationParameter.name} configuration value.
    */
    public void set${configurationParameter.name?cap_first}(${configurationParameter.type} ${configurationParameter.name}) {
        this.${configurationParameter.name} = ${configurationParameter.name};
    }
</#list>
}
