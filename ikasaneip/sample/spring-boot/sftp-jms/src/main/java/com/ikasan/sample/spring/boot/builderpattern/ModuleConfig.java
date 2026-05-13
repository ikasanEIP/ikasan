package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.connector.basefiletransfer.BaseFileTransferAutoConfiguration;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({ "classpath:ikasan-transaction-pointcut-jms.xml",
                    "classpath:h2-datasource-conf.xml"})
@Import(BaseFileTransferAutoConfiguration.class)
public class ModuleConfig
{
    @Autowired
    private BuilderFactory builderFactory;

    @Autowired
    private Flow sftpToJmsFlow;

    @Autowired
    private Flow jmsToSftpFlow;

    @Autowired
    private Flow sftpChunkingToJmsFlow;
    @Autowired
    private Flow jmsToSftpChunkingFlow;

    @Bean
    public Module getModule()
    {

        return builderFactory.getModuleBuilder("sample-boot-sftp-jms")
                             .withDescription("Sftp Jms Sample Module")
                             .addFlow(sftpToJmsFlow)
                             .addFlow(jmsToSftpFlow)
                             .addFlow(sftpChunkingToJmsFlow)
                             .addFlow(jmsToSftpChunkingFlow)
                             .build();

    }

}
