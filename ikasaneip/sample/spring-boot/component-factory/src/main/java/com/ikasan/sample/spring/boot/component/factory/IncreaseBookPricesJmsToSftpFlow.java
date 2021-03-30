package com.ikasan.sample.spring.boot.component.factory;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.ModuleBuilder;
import org.ikasan.component.converter.xml.XsltConverter;
import org.ikasan.component.endpoint.jms.spring.consumer.JmsContainerConsumer;
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

@Configuration("increaseBookPricesJmsToSftpFlowConfig")
public class IncreaseBookPricesJmsToSftpFlow
{
    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private IkasanComponentFactory ikasanComponentFactory;

    @Value("${module.name}")
    private String moduleName;

    @IkasanComponent(prefix="jms.esb.broker.shared", factoryPrefix = "increase.book.prices.jms.consumer")
    private JmsContainerConsumer increaseBookPricesJmsConsumer;

    @IkasanComponent(prefix="increase.book.prices.xslt.converter")
    private XsltConverter<String,String>increaseBookPricesXsltConverter;

    @IkasanComponent(prefix="increase.book.prices.xml.validator")
    private XMLValidator<String, String> increaseBookPricesXmlValidator;

    @IkasanComponent(prefix="increase.book.prices.sftp.producer")
    private SftpProducer increaseBookPricesSftpProducer;

    @Resource
    private ExceptionResolver exceptionResolver;

    @Bean
    public Flow increaseBookPricesJmsToSftpFlow(){
        ModuleBuilder mb = builderFactory.getModuleBuilder(moduleName);
        return mb.getFlowBuilder("Increase Book Prices Jms to Sftp Flow")
            .withDescription("Increases received book prices by 10% then uploads to a sftp directory")
            .withExceptionResolver(exceptionResolver)
            .consumer("Increase Book Prices Jms Consumer", increaseBookPricesJmsConsumer)
            .converter("Increase Book Prices Xslt Converter", increaseBookPricesXsltConverter)
            .converter("Increase Book Prices Xml Validator", increaseBookPricesXmlValidator)
            .producer("Increase Book Prices Sftp Producer", increaseBookPricesSftpProducer)
            .build();
    }
}
