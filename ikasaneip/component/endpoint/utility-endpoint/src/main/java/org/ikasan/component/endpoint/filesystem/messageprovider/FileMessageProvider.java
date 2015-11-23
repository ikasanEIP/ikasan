/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.apache.log4j.Logger;
import org.ikasan.spec.component.endpoint.EndpointListener;
import org.ikasan.spec.configuration.Configured;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.ikasan.component.endpoint.quartz.consumer.MessageProvider;

/**
 * Implementation of a MessageProvider based on returning a list of File references.
 *
 * @author Ikasan Development Team
 */
public class FileMessageProvider implements MessageProvider<List<File>>,
        ManagedResource, Configured<FileConsumerConfiguration>, EndpointListener<String,IOException>
{
    /** logger instance */
    private static Logger logger = Logger.getLogger(FileMessageProvider.class);

    /** path separator */
    private static final String FQN_PATH_SEPARATOR = "/";

    /** file consumer configuration */
    private FileConsumerConfiguration fileConsumerConfiguration;

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

    @Override
    public List<File> invoke(JobExecutionContext context)
    {
        List<File> files = new ArrayList<File>();
        filenames.clear();

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
                logger.info("Matching file names: " + files);
            }

            return files;
        }

        if(this.fileConsumerConfiguration.isLogMatchedFilenames())
        {
            logger.info("No matching file names");
        }

        return null;
    }

    public void setMessageProviderPostProcessor(MessageProviderPostProcessor messageProviderPostProcessor)
    {
        this.messageProviderPostProcessor = messageProviderPostProcessor;
    }

    @Override
    public FileConsumerConfiguration getConfiguration()
    {
        return fileConsumerConfiguration;
    }

    @Override
    public void setConfiguration(FileConsumerConfiguration fileConsumerConfiguration)
    {
        this.fileConsumerConfiguration = fileConsumerConfiguration;
        if(messageProviderPostProcessor != null && messageProviderPostProcessor instanceof Configured)
        {
            ((Configured)messageProviderPostProcessor).setConfiguration(this.fileConsumerConfiguration);
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
        return new FileMatcher(path, name, fileConsumerConfiguration.getDirectoryDepth(), this);
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
    public void startManagedResource()
    {
        if(fileConsumerConfiguration.getFilenames() != null)
        {
            for(String filename:fileConsumerConfiguration.getFilenames())
            {
                this.fileMatchers.add( getFileMatcher(filename) );
            }
        }

        logger.info("  - Started embedded managed component [FileMessageProvider]");
    }

    @Override
    public void stopManagedResource()
    {
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
