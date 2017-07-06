package org.ikasan.sample.spring.boot;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.jta.narayana.NarayanaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ImportResource( {

/**
 *    <value>monitor-conf.xml</value>

 <value>ikasan-transaction-pointcut-jms.xml</value>

 <value>logger-conf.xml</value>
 <value>shared-conf.xml</value>
 <value>exception-conf.xml</value>

 */
        "classpath:monitor-service-conf.xml",
        "classpath:monitor-conf.xml",
        "classpath:ikasan-transaction-pointcut-jms.xml",
        "classpath:logger-conf.xml",
        "classpath:exception-conf.xml",
        "classpath:jms-conf.xml",


} )
public class ModuleConfig {

    @Resource
    private JmsContainerConsumer jmsConsumer;

    @Resource
    private JmsTemplateProducer jmsProducer;

    @Resource
    private AutowireCapableBeanFactory beanFactory;

    /**
     *
     *
     * <bean id="sourceFlow"                   class="org.ikasan.builder.FlowFactory">
     <property name="moduleName"         ref="moduleName" />
     <property name="name"               value="JMS Source" />
     <property name="description"        value="JMS Source Description" />
     <property name="exceptionResolver"  ref="exceptionResolver" />
     <property name="ikasanSerialiserFactory" ref="ikasanSerialiserFactory" />
     <property name="consumer">
     <bean class="org.ikasan.builder.FlowElementFactory">
     <property name="name"       value="JMS Consumer"/>
     <property name="component"  ref="jmsConsumer"/>
     <property name="transition" ref="jmsProducerFlowElement"/>
     </bean>
     </property>
     <property name="monitor" ref="monitor"/>
     </bean>

     <bean id="jmsProducerFlowElement" class="org.ikasan.builder.FlowElementFactory">
     <property name="name"               value="JMS Producer"/>
     <property name="component"          ref="jmsProducer" />
     </bean>


     * @return
     */
    @Bean
    public Module getModule(){


        FlowBuilder fb = BuilderFactory.flowBuilder("flowName", "sample-module");
        beanFactory.autowireBean(fb);
        Flow flow = fb
                .withDescription("flowDescription")
                .consumer("consumer", jmsConsumer)
                .producer("producer", jmsProducer).build();

        Module module = BuilderFactory.moduleBuilder("sample-module").withDescription("Sample Module").addFlow(flow).build();
        return module;
    }




}
