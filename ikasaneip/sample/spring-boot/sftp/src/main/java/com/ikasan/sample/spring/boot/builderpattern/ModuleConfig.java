package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.*;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.endpoint.quartz.consumer.ScheduledConsumer;
import org.ikasan.component.endpoint.util.producer.DevNull;
import com.ikasan.sample.converter.PayloadToStringConverter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import javax.annotation.Resource;

@Configuration
@ImportResource( {

        "classpath:monitor-service-conf.xml",
        "classpath:monitor-conf.xml",
        "classpath:ikasan-transaction-pointcut-quartz.xml",
        "classpath:sftp-to-log-component-conf.xml",
        "classpath:filetransfer-service-conf.xml",
        "classpath:h2-datasource-conf.xml"


} )
public class ModuleConfig {

    @Resource
    private ScheduledConsumer fileGeneratorScheduledConsumer;

    @Resource
    private Converter filePayloadGeneratorConverter;

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

    @Value("${sftp.producer.clientID}")
    private String sftpProducerClientID;

    @Value("${sftp.producer.username}")
    private String sftpProducerUsername;

    @Value("${sftp.producer.password}")
    private String sftpProducerPassword;

    @Value("${sftp.producer.remoteHost}")
    private String sftpProducerRemoteHost;

    @Value("${sftp.producer.remotePort}")
    private Integer sftpProducerRemotePort;

    @Value("${sftp.producer.outputDirectory}")
    private String sftpConsumerOutputDirectory;

    @Bean
    public Module getModule(){

        ModuleBuilder mb = builderFactory.getModuleBuilder("sample-boot-sftp-module");



        Flow timeGeneratorToSftpFlow = getTimeGeneratorToSftpFlow(mb,builderFactory.getComponentBuilder());
        Flow sftpToLogFlow = getSftpConsumerFlow(mb,builderFactory.getComponentBuilder());

        Module module = mb.withDescription("SFTP Sample Module")
                .addFlow(sftpToLogFlow).addFlow(timeGeneratorToSftpFlow).build();
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
                        .setScheduledJobGroupName("SftpToLogFlow")
                        .setScheduledJobName("SftpConsumer")
                        .build())
                .converter("SFTP payload to String Converter",new PayloadToStringConverter())
                .producer("Log", new DevNull()).build();
        return sftpToLogFlow;
    }

    /**
     *

     <bean id="sftpProducerConfiguration" class="org.ikasan.endpoint.sftp.producer.SftpProducerConfiguration">
     <property name="clientID" value="${sftp.producer.clientID}"/>
     <property name="username" value="${sftp.producer.username}"/>
     <property name="password" value="${sftp.producer.password}"/>
     <property name="remoteHost" value="${sftp.producer.remoteHost}"/>
     <property name="remotePort" value="${sftp.producer.remotePort}"/>
     <property name="outputDirectory" value="${sftp.producer.outputDirectory}"/>
     <property name="cleanupJournalOnComplete" value="false"/>
     </bean>


     * @param moduleBuilder
     * @param componentBuilder
     * @return
     */
    public Flow getTimeGeneratorToSftpFlow(ModuleBuilder moduleBuilder, ComponentBuilder componentBuilder)
    {
        FlowBuilder timeGeneratorToSftpFlowBuilder = moduleBuilder.getFlowBuilder("TimeGenerator To Sftp Flow");
        Flow timeGeneratorToSftpFlow = timeGeneratorToSftpFlowBuilder
                .withDescription("Generates random string and send it to sftp as file")
                .consumer("Scheduled Consumer", fileGeneratorScheduledConsumer)
                .converter("Random String Generator",filePayloadGeneratorConverter)
                .producer("Sftp Producer", componentBuilder.sftpProducer()
                        .setClientID(sftpProducerClientID)
                        .setUsername(sftpProducerUsername)
                        .setPassword(sftpProducerPassword)
                        .setRemoteHost(sftpProducerRemoteHost)
                        .setRemotePort(sftpProducerRemotePort)
                        .setOutputDirectory(sftpConsumerOutputDirectory)
                        .setConfiguredResourceId("sftpProducerConfiguration")
                        .build())
                .build();

        return timeGeneratorToSftpFlow;
    }


}
