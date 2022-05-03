package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.component.endpoint.filesystem.messageprovider.FileMatcher;
import org.ikasan.component.endpoint.filesystem.messageprovider.MessageProviderPostProcessor;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextParametersCacheUtil;
import org.ikasan.ootb.scheduler.agent.module.configuration.ContextualisedFileConsumerConfiguration;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.event.ForceTransactionRollbackException;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ContextualisedFileMessageProvider implements MessageProvider<List<File>>,
    ManagedResource, Configured<ContextualisedFileConsumerConfiguration>, EndpointListener<String, IOException>
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(ContextualisedFileMessageProvider.class);

    /** path separator */
    private static final String FQN_PATH_SEPARATOR = "/";

    /** file consumer configuration */
    private ContextualisedFileConsumerConfiguration fileConsumerConfiguration;

    /** list of file matchers to be invoked */
    List<FileMatcher> fileMatchers = new ArrayList<>();

    /** criticality for this resource */
    boolean criticalOnStartup;

    /** handle to the recovery manager for escalating failures */
    ManagedResourceRecoveryManager managedResourceRecoveryManager;

    /** record all filenames returned from the fileMatcher */
    List<String> filenames = new ArrayList<>();

    /** post processor on the read files */
    MessageProviderPostProcessor messageProviderPostProcessor;

    /** maintain a control state to coordinate the stopping of any processing */
    boolean active;

    @Override
    public List<File> invoke(JobExecutionContext context)
    {
        List<File> files = new ArrayList<File>();
        filenames.clear();

        createFileMatchers();

        try
        {
            for(FileMatcher fileMatcher:fileMatchers)
            {
                try
                {
                    fileMatcher.invoke();
                }
                catch(IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        catch(ConcurrentModificationException e)
        {
            if(isActive())
            {
                throw e;
            }

            throw new ForceTransactionRollbackException("File processing interrupted by a stop request.");
        }

        for(String filename:filenames)
        {
            File file = new File(filename);
            files.add(file);
        }

        if(this.messageProviderPostProcessor != null)
        {
            this.messageProviderPostProcessor.invoke(files);
        }

        if(files.size() > 0)
        {
            if(this.fileConsumerConfiguration.isLogMatchedFilenames())
            {
                for(File file:files)
                {
                    if(logger.isInfoEnabled())
                    {
                        logger.info("Matching filename for " + file.getAbsolutePath());
                    }
                }
            }

            return files;
        }

        if(this.fileConsumerConfiguration.isLogMatchedFilenames())
        {
            for(String filename:this.fileConsumerConfiguration.getFilenames())
            {
                if(logger.isInfoEnabled())
                {
                    if(filename.startsWith("/"))
                    {
                        logger.info("No matching filename for " + filename);
                    }
                    else
                    {
                        logger.info("No matching filename for " + System.getProperty("user.dir") + "/" + filename);
                    }
                }
            }
        }

        return null;
    }

    public void setMessageProviderPostProcessor(MessageProviderPostProcessor messageProviderPostProcessor)
    {
        this.messageProviderPostProcessor = messageProviderPostProcessor;
    }

    public MessageProviderPostProcessor getMessageProviderPostProcessor()
    {
        return this.messageProviderPostProcessor;
    }

    @Override
    public ContextualisedFileConsumerConfiguration getConfiguration()
    {
        return fileConsumerConfiguration;
    }

    @Override
    public void setConfiguration(ContextualisedFileConsumerConfiguration fileConsumerConfiguration)
    {
        this.fileConsumerConfiguration = fileConsumerConfiguration;
        if(messageProviderPostProcessor != null && messageProviderPostProcessor instanceof Configured)
        {
            ((Configured)messageProviderPostProcessor).setConfiguration(this.fileConsumerConfiguration);
        }
    }

    /** replacing contextual param with correct value in the filenames */
    private void createFileMatchers() {
        fileMatchers.clear();
        for(String filename : fileConsumerConfiguration.getFilenames())
        {
            this.fileMatchers.add( getFileMatcher(ContextParametersCacheUtil.resolveContextualPlaceholderParam(fileConsumerConfiguration.getContextId(), filename)) );
        }
    }

    protected FileMatcher getFileMatcher(String fullyQualifiedFilename)
    {
        if( !fullyQualifiedFilename.startsWith("/") && !fullyQualifiedFilename.startsWith("."))
        {
            // assume relative reference and prefix accordingly
            fullyQualifiedFilename = "./" + fullyQualifiedFilename;
        }

        int lastIndexOffullPath = fullyQualifiedFilename.lastIndexOf(FQN_PATH_SEPARATOR);
        String path = fullyQualifiedFilename.substring(0,lastIndexOffullPath);
        String name = fullyQualifiedFilename.substring(++lastIndexOffullPath);
        return new FileMatcher(this.fileConsumerConfiguration.isIgnoreFileRenameWhilstScanning(), path, name, fileConsumerConfiguration.getDirectoryDepth(), this);
    }

    @Override
    public void onMessage(String filename)
    {
        filenames.add(filename);
    }

    @Override
    public void onException(IOException throwable)
    {
        managedResourceRecoveryManager.recover(throwable);
    }

    @Override
    public boolean isActive()
    {
        return this.active;
    }

    @Override
    public void startManagedResource()
    {
        this.active = true;
        logger.info("  - Started embedded managed component [FileMessageProvider]");
    }

    @Override
    public void stopManagedResource()
    {
        this.active = false;
        this.fileMatchers.clear();
        logger.info("  - Stopped embedded managed component [FileMessageProvider]");
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {
        this.managedResourceRecoveryManager = managedResourceRecoveryManager;
    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return criticalOnStartup;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {
        this.criticalOnStartup = criticalOnStartup;
    }
}
