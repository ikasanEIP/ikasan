package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.*;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.util.producer.DevNull;
import org.ikasan.sample.converter.PayloadToStringConverter;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ImportResource( {

        "classpath:monitor-service-conf.xml",
        "classpath:monitor-conf.xml",
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:logger-conf.xml",
        "classpath:exception-conf.xml",
        "classpath:sftp-to-log-component-conf.xml",
        "classpath:filetransfer-service-conf.xml"


} )
public class ModuleConfig {

    @Resource
    private ScheduledConsumer sftpConsumer;

    @Resource
    private ScheduledConsumer fileGeneratorScheduledConsumer;

    @Resource
    private Converter payloadToStringConverter;
    @Resource
    private Converter filePayloadGeneratorConverter;

    @Resource
    private Producer sftpProducer;

    @Resource
    private ApplicationContext context;

    @Resource
    private BuilderFactory builderFactory;

    @Value("${sftp.consumer.cronExpression}")
    private String sftpConsumerCronExpression;

    @Value("${sftp.consumer.clientID}")
    private String sftpConsumerClientID;

    @Value("${sftp.consumer.username}")
    private String sftpConsumerUsername;

    @Value("${sftp.consumer.password}")
    private String sftpConsumerPassword;

    @Value("${sftp.consumer.remoteHost}")
    private String sftpConsumerRemoteHost;

    @Value("${sftp.consumer.remotePort}")
    private Integer sftpConsumerRemotePort;

    @Value("${sftp.consumer.sourceDirectory}")
    private String sftpConsumerSourceDirectory;

    @Value("${sftp.consumer.filenamePattern}")
    private String sftpConsumerFilenamePattern;

    @Value("${sftp.consumer.knownHosts}")
    private String sftpConsumerKnownHosts;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-sftp-module");

        FlowBuilder timeGeneratorToSftpFlowBuilder = mb.getFlowBuilder("timeGeneratorToSftpFlow");
        Flow timeGeneratorToSftpFlow = timeGeneratorToSftpFlowBuilder
                .withDescription("Generates random string and send it to sftp as file")
                .consumer("Scheduled Consumer", fileGeneratorScheduledConsumer)
                .converter("Random String Generator",filePayloadGeneratorConverter)
                .producer("Sftp Producer", sftpProducer)
                .build();

        Flow sftpToLogFlow = getSftpConsumerFlow(mb,builderFactory.getComponentBuilder());

        Module module = mb.withDescription("SFTP Sample Module")
                .addFlow(timeGeneratorToSftpFlow).addFlow(sftpToLogFlow).build();
        return module;

    }

    /**
     *  <bean id="sftpConsumerConfiguration" class="org.ikasan.endpoint.sftp.consumer.SftpConsumerConfiguration">
     <property name="cronExpression" value="${sftp.consumer.cronExpression}"/>
     <property name="clientID" value="${sftp.consumer.clientID}"/>
     <property name="username" value="${sftp.consumer.username}"/>
     <property name="password" value="${sftp.consumer.password}"/>
     <property name="remoteHost" value="${sftp.consumer.remoteHost}"/>
     <property name="remotePort" value="${sftp.consumer.remotePort}"/>
     <property name="sourceDirectory" value="${sftp.consumer.sourceDirectory}"/>
     <property name="filenamePattern" value="${sftp.consumer.filenamePattern}"/>
     <property name="chronological" value="true"/>
     <property name="cleanupJournalOnComplete" value="false"/>
     <property name="ageOfFiles" value="30"/>
     <property name="minAge" value="1"/>
     <property name="filterOnLastModifiedDate" value="false"/>
     <property name="filterDuplicates" value="true"/>
     <property name="renameOnSuccess" value="false"/>
     <property name="renameOnSuccessExtension" value=".tmp"/>
     <property name="destructive" value="false"/>
     <property name="chunking" value="false"/>
     </bean>

     * @param moduleBuilder
     * @param componentBuilder
     * @return
     */
    public Flow getSftpConsumerFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        FlowBuilder sftpToLogFlowBuilder = moduleBuilder.getFlowBuilder("Sftp To Log Flow");
        Flow sftpToLogFlow = sftpToLogFlowBuilder
                .withDescription("Sftp to Log")
                .consumer("Sftp Consumer", componentBuilder.sftpConsumer()
                        .setCronExpression(sftpConsumerCronExpression)
                        .setClientID(sftpConsumerClientID)
                        .setUsername(sftpConsumerUsername)
                        .setPassword(sftpConsumerPassword)
                        .setRemoteHost(sftpConsumerRemoteHost)
                        .setRemotePort(sftpConsumerRemotePort)
                        .setSourceDirectory(sftpConsumerSourceDirectory)
                        .setFilenamePattern(sftpConsumerFilenamePattern)
                        .setKnownHostsFilename(sftpConsumerKnownHosts)
                        .setChronological(true)
                        .setCleanupJournalOnComplete(false)
                        .setAgeOfFiles(30)
                        .setMinAge(1l)
                        .setFilterDuplicates(true)
                        .setFilterOnLastModifiedDate(true)
                        .setRenameOnSuccess(false)
                        .setRenameOnSuccessExtension(".tmp")
                        .setDestructive(false)
                        .setChunking(false)
                        .setConfiguredResourceId("configuredResourceId")
                        .build())
                .converter("SFTP payload to String Converter",new PayloadToStringConverter())
                .producer("Log", new DevNull()).build();
        return sftpToLogFlow;
    }

}
