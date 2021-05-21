package org.ikasan.component.factory.spring.endpoint;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.component.factory.spring.BaseComponentFactory;
import org.ikasan.endpoint.sftp.consumer.SftpConsumer;
import org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration;
import org.ikasan.endpoint.sftp.producer.SftpProducer;
import org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SftpConsumerComponentFactory extends BaseComponentFactory<SftpConsumer>
{

    @Resource
    protected BuilderFactory builderFactory;

    @Override public SftpConsumer create(String nameSuffix, String configPrefix, String factoryConfigPrefix)
    {
        return (SftpConsumer) builderFactory.getComponentBuilder()
            .sftpConsumer()
            .setConfiguration(configuration(configPrefix, SftpConsumerConfiguration.class))
            .setConfiguredResourceId(configuredResourceId(nameSuffix))
            .setScheduledJobGroupName(nameSuffix + "-JobGroup")
            .setScheduledJobName(nameSuffix + "-JobName")
            .build();
    }
}
