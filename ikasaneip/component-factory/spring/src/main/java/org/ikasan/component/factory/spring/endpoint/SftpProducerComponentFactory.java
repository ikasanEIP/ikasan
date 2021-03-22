package org.ikasan.component.factory.spring.endpoint;

import liquibase.pro.packaged.S;
import liquibase.pro.packaged.T;
import org.apache.catalina.webresources.TomcatURLStreamHandlerFactory;
import org.apache.xalan.xsltc.trax.TransformerFactoryImpl;
import org.ikasan.builder.BuilderFactory;
import org.ikasan.builder.component.endpoint.SftpProducerBuilderImpl;
import org.ikasan.component.converter.xml.XsltConverter;
import org.ikasan.component.converter.xml.XsltConverterConfiguration;
import org.ikasan.component.factory.spring.BaseComponentFactory;
import org.ikasan.endpoint.sftp.producer.SftpProducer;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;

import javax.annotation.Resource;

public class SftpProducerComponentFactory extends BaseComponentFactory<SftpProducer>
{

    @Resource
    protected BuilderFactory builderFactory;

    @Override public SftpProducer create(String nameSuffix, String configPrefix, String factoryConfigPrefix)
    {
        return (SftpProducer)builderFactory.getComponentBuilder()
            .sftpProducer()
            .setConfiguration(configuration(configPrefix, SftpProducerConfiguration.class))
            .setConfiguredResourceId(configuredResourceId(nameSuffix))
            .build();
    }
}
