package org.ikasan.component.factory.spring.endpoint;

import org.ikasan.builder.BuilderFactory;
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
