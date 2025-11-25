package ${moduleBasePackage}.component;

import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.routing.MultiRecipientRouter;
import org.ikasan.spec.component.routing.SingleRecipientRouter;
import org.ikasan.spec.component.sequencing.Sequencer;
import org.ikasan.spec.component.splitting.Splitter;
import org.ikasan.spec.component.transformation.Translator;
import org.ikasan.spec.component.endpoint.Broker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class ScaffoldingComponentFactory {

<#compress>
    <#list flowElementMetaData as component>
        @Resource
        @Qualifier("${component.componentName?replace(" ", "")?replace(",", "")?uncap_first}")
        <#if component.componentType == "org.ikasan.spec.component.endpoint.Consumer">
            private Consumer ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Producer">
            private Producer ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.filter.Filter">
            private Filter ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.transformation.Converter">
            private Converter ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.routing.MultiRecipientRouter">
            private MultiRecipientRouter ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.routing.SingleRecipientRouter">
            private SingleRecipientRouter ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.sequencing.Sequencer">
            private Sequencer ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.splitting.Splitter">
            private Splitter ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.transformation.Translator">
            private Translator ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Broker">
            private Broker ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
        </#if>
    </#list>
</#compress>


<#list flowElementMetaData as component>
    <#if component.componentType == "org.ikasan.spec.component.endpoint.Consumer">
    /**
    * This method returns the Consumer associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Consumer bean.
    */
    public Consumer get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Producer">
    /**
    * This method returns the Producer associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Producer bean.
    */
    public Producer get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.filter.Filter">
    /**
    * This method returns the Filter associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Filter bean.
    */
    public Filter get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.transformation.Converter">
    /**
    * This method returns the Converter associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Converter bean.
    */
    public Converter get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.routing.MultiRecipientRouter">
    /**
    * This method returns the MultiRecipientRouter associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} MultiRecipientRouter bean.
    */
    public MultiRecipientRouter get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.routing.SingleRecipientRouter">
    /**
    * This method returns the SingleRecipientRouter associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} SingleRecipientRouter bean.
    */
    public SingleRecipientRouter get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.sequencing.Sequencer">
    /**
    * This method returns the Sequencer associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Sequencer bean.
    */
    public Sequencer get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.splitting.Splitter">
    /**
    * This method returns the Splitter associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Splitter bean.
    */
    public Splitter get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.transformation.Translator">
    /**
    * This method returns the Translator associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Translator bean.
    */
    public Translator get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    <#elseif component.componentType == "org.ikasan.spec.component.endpoint.Broker">
    /**
    * This method returns the Broker associated with the ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} bean.
    *
    * @return The ${component.componentName?replace(" ", "")?replace(",", "")?uncap_first} Broker bean.
    */
    public Broker get${component.componentName?replace(" ", "")?replace(",", "")}() {
        return this.${component.componentName?replace(" ", "")?replace(",", "")?uncap_first};
    }

    </#if>
</#list>
}
