package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.BuilderFactory;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ImportResource({ "classpath:ikasan-transaction-pointcut-jms.xml",
                    "classpath:filetransfer-service-conf.xml",
                    "classpath:h2-datasource-conf.xml"

                })
public class ModuleConfig
{
    @Resource
    private BuilderFactory builderFactory;

    @Resource
    private Flow sftpToJmsFlow;

    @Resource
    private Flow jmsToSftpFlow;

    @Resource
    private Flow sftpChunkingToJmsFlow;
    @Resource
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
