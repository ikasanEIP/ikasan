/*
 * $Id: MQJmsProducer.java 42504 2015-01-20 19:49:30Z majean $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/enterpriseServiceBus/trunk/projects/mq/connector-mq/adapter/client/src/main/java/com/mizuho/cmi/connector/endpoint/client/mq/producer/MQJmsProducer.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2014 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 */
package org.ikasan.endpoint.ftp.producer;


import org.apache.log4j.Logger;
import org.ikasan.client.FileTransferConnectionTemplate;
import org.ikasan.common.Payload;
import org.ikasan.connector.ftp.outbound.FTPConnectionSpec;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import javax.resource.ResourceException;
import javax.resource.cci.ConnectionFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * FTP Implementation of a producer based on the JCA specification.
 *
 * @author Middleware Team
 */
public class FtpProducer implements Producer<Payload>,
        ManagedResource, ConfiguredResource<FtpProducerConfiguration> {
    /**
     * class logger
     */
    private static Logger logger = Logger.getLogger(FtpProducer.class);

    /**
     * Connection factory
     */
    private final ConnectionFactory connectionFactory;

    /**
     * configured resource id
     */
    protected String configuredResourceId;

    /**
     * Configuration - default to vanilla instance
     */
    protected FtpProducerConfiguration configuration = new FtpProducerConfiguration();


    /**
     * Currently active connection template
     */
    protected FileTransferConnectionTemplate activeFileTransferConnectionTemplate;

    /**
     * A connection template
     */
    protected FileTransferConnectionTemplate fileTransferConnectionTemplate;

    /**
     * Alternate template to be used in cases of failure
     */
    protected FileTransferConnectionTemplate alternateFileTransferConnectionTemplate;


    /**
     * determines whether this managed resource failure will fail the startup of the flow
     */
    protected boolean isCriticalOnStartup = true;

    /**
     * Constructor
     *
     * @param connectionFactory FTP connection factory
     */
    public FtpProducer(final ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        if (this.connectionFactory == null) {
            throw new IllegalArgumentException("connectionFactory cannot be 'null'");
        }

    }

    public FtpProducerConfiguration getConfiguration() {
        return this.configuration;
    }

    public String getConfiguredResourceId() {
        return this.configuredResourceId;
    }

    public void setConfiguration(FtpProducerConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    public void invoke(Payload payload) throws EndpointException {

            try {
                // Leave this map empty if the output directory should be used in all cases
                Map<String, String> outputTargets = new HashMap<String, String>();

                this.activeFileTransferConnectionTemplate.deliverPayload(
                        payload,
                        this.configuration.getOutputDirectory(),
                        outputTargets,
                        this.configuration.getOverwrite().booleanValue(),
                        this.configuration.getRenameExtension(),
                        this.configuration.getChecksumDelivered().booleanValue(),
                        this.configuration.getUnzip().booleanValue(),
                        this.configuration.getCleanupJournalOnComplete()
                );
            } catch (ResourceException e) {
                this.switchActiveConnection();
                throw new EndpointException(e);
            }

    }

    /**
     * Switch the active connection to the other connection template.
     */
    protected void switchActiveConnection() {
        if (this.alternateFileTransferConnectionTemplate != null) {
            if (this.activeFileTransferConnectionTemplate == this.fileTransferConnectionTemplate) {
                this.activeFileTransferConnectionTemplate = this.alternateFileTransferConnectionTemplate;
            } else {
                this.activeFileTransferConnectionTemplate = this.fileTransferConnectionTemplate;
            }
        }
    }

    /* (non-Javadoc)
    * @see org.ikasan.spec.management.ManagedResource#startManagedResource()
    */
    @Override
    public void startManagedResource() {
        createEndpoint(configuration);

    }

    /* (non-Javadoc)
     * @see org.ikasan.spec.management.ManagedResource#stopManagedResource()
     */
    public void stopManagedResource() {
    }

    public boolean isCriticalOnStartup() {
        return this.isCriticalOnStartup;
    }

    public void setCriticalOnStartup(boolean isCriticalOnStartup) {
        this.isCriticalOnStartup = isCriticalOnStartup;
    }

    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {
        // dont check this by default
    }

    /**
     * Utility method to aid testing of this class
     *
     * @return
     */
    protected FTPConnectionSpec getConnectionSpec() {
        return new FTPConnectionSpec();
    }

    /* (non-Jsdoc)
    * @see org.ikasan.spec.endpoint.EndpointFactory#createEndpoint(java.lang.Object)
    */
    private void createEndpoint(FtpProducerConfiguration configuration) {
        configuration.validate();

        FTPConnectionSpec spec = this.getConnectionSpec();
        spec.setClientID(configuration.getClientID());
        spec.setRemoteHostname(configuration.getRemoteHost());
        spec.setMaxRetryAttempts(configuration.getMaxRetryAttempts());
        spec.setRemotePort(configuration.getRemotePort());
        spec.setConnectionTimeout(configuration.getConnectionTimeout());
        spec.setUsername(configuration.getUsername());
        spec.setCleanupJournalOnComplete(configuration.getCleanupJournalOnComplete());
        spec.setActive(configuration.getActive());
        spec.setPassword(configuration.getPassword());
        spec.setDataTimeout(configuration.getDataTimeout());
        spec.setSocketTimeout(configuration.getSocketTimeout());
        spec.setSystemKey(configuration.getSystemKey());

        FTPConnectionSpec alternateSpec = null;
        if (configuration instanceof FtpProducerAlternateConfiguration) {
            FtpProducerAlternateConfiguration alternteConfig = (FtpProducerAlternateConfiguration) configuration;
            alternateSpec = this.getConnectionSpec();
            alternateSpec.setClientID(alternteConfig.getClientID());
            alternateSpec.setRemoteHostname(alternteConfig.getAlternateRemoteHost());
            alternateSpec.setMaxRetryAttempts(alternteConfig.getAlternateMaxRetryAttempts());
            alternateSpec.setRemotePort(alternteConfig.getAlternateRemotePort());
            alternateSpec.setConnectionTimeout(alternteConfig.getAlternateConnectionTimeout());
            alternateSpec.setUsername(alternteConfig.getAlternateUsername());
            alternateSpec.setCleanupJournalOnComplete(alternteConfig.getCleanupJournalOnComplete());
            alternateSpec.setActive(alternteConfig.getAlternateActive());
            alternateSpec.setPassword(alternteConfig.getAlternatePassword());
            alternateSpec.setDataTimeout(alternteConfig.getAlternateDataTimeout());
            alternateSpec.setSocketTimeout(alternteConfig.getAlternateSocketTimeout());
            alternateSpec.setSystemKey(alternteConfig.getAlternateSystemKey());
        }

        this.getEndpoint(spec, alternateSpec);
    }


    /**
     * Internal endpoint creation method allows for easier overriding of the actual endpoint creation and simpler testing.
     *
     * @param spec
     * @param alternateSpec
     * @return
     */
    private void getEndpoint(final FTPConnectionSpec spec, final FTPConnectionSpec alternateSpec) {
        activeFileTransferConnectionTemplate = new FileTransferConnectionTemplate(this.connectionFactory, spec);
        // activeFileTransferConnectionTemplate.addListener(this.listener);


        if (alternateSpec != null) {
            alternateFileTransferConnectionTemplate = new FileTransferConnectionTemplate(this.connectionFactory, spec);
            // alternateFileTransferConnectionTemplate.addListener(this.listener);

        }
    }
}
