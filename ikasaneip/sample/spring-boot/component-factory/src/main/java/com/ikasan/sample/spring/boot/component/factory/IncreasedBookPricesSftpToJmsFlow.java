package com.ikasan.sample.spring.boot.component.factory;

import com.ikasan.sample.spring.boot.component.factory.custom.converter.PayloadToStringConverter;
import com.ikasan.sample.spring.boot.component.factory.custom.converter.StringToPayloadConverter;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.component.converter.xml.XsltConverter;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
import org.ikasan.component.endpoint.jms.spring.producer.JmsTemplateProducer;
import org.ikasan.component.factory.spring.IkasanComponentFactory;
import org.ikasan.component.factory.spring.annotation.IkasanComponent;
import org.ikasan.component.validator.xml.XMLValidator;
import org.ikasan.endpoint.sftp.consumer.SftpConsumer;
import org.ikasan.endpoint.sftp.producer.SftpProducer;
import org.ikasan.exceptionResolver.ExceptionResolver;
import org.ikasan.spec.flow.Flow;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration("increasedBookPricesSftpToJmsFlowConfig")
public class IncreasedBookPricesSftpToJmsFlow
{
    @Resource
    private BuilderFactory builderFactory;


    @Value("${module.name}")
    private String moduleName;

    @IkasanComponent(prefix="increased.book.prices.sftp.consumer")
    private SftpConsumer increasedBookPricesSftpConsumer;

    @IkasanComponent
    private PayloadToStringConverter payloadToStringConverter;

    @IkasanComponent(prefix="jms.esb.broker.shared", factoryPrefix = "increased.book.prices.jms.producer")
    private JmsTemplateProducer<String> increasedBookPricesJmsProducer;


    @Resource
    private ExceptionResolver exceptionResolver;

    @Bean
    public Flow increasedBookPricesSftpToJmsFlow(){
        ModuleBuilder mb = builderFactory.getModuleBuilder(moduleName);
        return mb.getFlowBuilder("Increased Book Prices Sftp to Jms Flow")
            .withDescription("Downloads increased book prices from sftp and publishes them to an internal jms queue")
            .withExceptionResolver(exceptionResolver)
            .consumer("Increased Book Prices Sftp Consumer", increasedBookPricesSftpConsumer)
            .converter("Increased Book Prices Payload to String Converter", payloadToStringConverter)
            .producer("Increased Book Prices Jms Producer", increasedBookPricesJmsProducer)
            .build();
    }
}
