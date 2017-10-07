package org.ikasan.sample.spring.boot.builderpattern;

import org.apache.activemq.ActiveMQXAConnectionFactory;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.FlowBuilder;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;

@Configuration
@ImportResource( {

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
    private ApplicationContext context;

    @Resource
    private BuilderFactory builderFactory;

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

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-module");

        FlowBuilder fb = mb.getFlowBuilder("flowName");

        ConnectionFactory connectionFactory = new ActiveMQXAConnectionFactory("failover:(vm://embedded-broker?create=false)");
        Consumer localJmsConsumer = builderFactory.getComponentBuilder().jmsConsumer()
                .setConnectionFactory(connectionFactory)
                .setDestinationJndiName("source")
                .setAutoContentConversion(true)
                .build();

        Flow flow = fb
                .withDescription("flowDescription")
                .consumer("consumer", localJmsConsumer)     // jmsConsumer
                .producer("producer", jmsProducer).build();

        Module module = mb.withDescription("Sample Module").addFlow(flow).build();
        return module;
    }




}
