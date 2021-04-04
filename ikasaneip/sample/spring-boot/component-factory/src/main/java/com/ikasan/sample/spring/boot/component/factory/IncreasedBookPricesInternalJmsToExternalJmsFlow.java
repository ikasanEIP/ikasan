package com.ikasan.sample.spring.boot.component.factory;

import com.ikasan.sample.spring.boot.component.factory.custom.converter.StringToPayloadConverter;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.component.converter.xml.XsltConverter;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.factory.spring.IkasanComponentFactory;
import org.ikasan.component.factory.spring.annotation.IkasanComponent;
import org.ikasan.component.validator.xml.XMLValidator;
import org.ikasan.endpoint.sftp.producer.SftpProducer;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration("increasedBookPricesInternalJmsToExternalJmsFlowConfig")
public class IncreasedBookPricesInternalJmsToExternalJmsFlow
{
    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private IkasanComponentFactory ikasanComponentFactory;

    @Value("${module.name}")
    private String moduleName;

    @IkasanComponent(prefix="jms.esb.broker.shared", factoryPrefix = "increased.book.prices.internal.jms.consumer")
    private JmsContainerConsumer increasedBookPricesInternalJmsConsumer;

    @IkasanComponent(prefix="jms.esb.broker.shared", factoryPrefix = "increased.book.prices.external.jms.producer")
    private JmsTemplateProducer<String> increasedBookPricesExternalJmsProducer;

    @Resource
    private ExceptionResolver exceptionResolver;

    @Bean
    public Flow increasedBookPricesInternalJmsToExternalFlow(){
        ModuleBuilder mb = builderFactory.getModuleBuilder(moduleName);
        return mb.getFlowBuilder("Increased Book Prices Internal Jms to External Jms Flow")
            .withDescription("Increases received book prices by 10% then uploads to a sftp directory")
            .withExceptionResolver(exceptionResolver)
            .consumer("Increased Book Prices Internal Jms Consumer", increasedBookPricesInternalJmsConsumer)
            .producer("Increased Book Prices External Jms Producer", increasedBookPricesExternalJmsProducer)
            .build();
    }
}
