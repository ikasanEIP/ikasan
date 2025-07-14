package ${packageName};

public class ${className} {
<#list configuration as key, value>
    private ${value.type} ${key};
</#list>

<#list configuration as key, value>
    public ${value.type} get${key?cap_first}() {
        return ${key};
    }

    public void set${key?cap_first}(${value.type} ${key}) {
        this.${key} = ${key};
    }
</#list>
}
