package org.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.builder.*;
import org.ikasan.builder.component.ComponentBuilder;
import org.ikasan.component.endpoint.util.producer.DevNull;
import org.ikasan.sample.converter.PayloadToStringConverter;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;

public class Application
{

    public static void main(String[] args) throws Exception
    {
        new Application().executeIM(args);
        System.out.println("Context ready");
    }


    public void executeIM(String[] args)
    {
        // get an ikasanApplication instance
        IkasanApplication ikasanApplication = IkasanApplicationFactory.getIkasanApplication(args);

        // get a builderFactory
        BuilderFactory builderFactory = ikasanApplication.getBuilderFactory();

        // get a module builder from the ikasanApplication
        ModuleBuilder moduleBuilder = builderFactory.getModuleBuilder("sample-boot-sftp-module");

        // get an instance of flowBuilder from the moduleBuilder and create a flow
        Flow sftpFlow = getSftpConsumerFlow(moduleBuilder, builderFactory.getComponentBuilder());

        // add flows to the module
        Module module = moduleBuilder.addFlow(sftpFlow).build();

        // pass the module to Ikasan to run
        ikasanApplication.run(module);

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
                        .setCronExpression("0/5 * * * * ?")
                        .setClientID("Sftp Consumer")
                        .setUsername("test")
                        .setPassword("test")
                        .setRemoteHost("localhost")
                        .setRemotePort(22999)
                        .setSourceDirectory(".")
                        .setFilenamePattern(".*txt")
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