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
package org.ikasan.archetype;

import org.apache.maven.shared.invoker.*;
import org.apache.maven.shared.utils.cli.CommandLineException;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Properties;

/**
 * Abstract class representing the base for a Maven Command.
 *
 * A command fails with a {@link MavenInvocationException} when the invoked Maven build
 * does not complete successfully; the tail of the build output is included in the message.
 * 
 * @author Ikasan Development Team
 */
public class AbstractMavenCommand<RESULT> implements MavenCommand<RESULT>
{
    /** number of trailing output lines reported on a failed invocation */
    static final int OUTPUT_TAIL_LINES = 150;

    List<String> goals;

    boolean debug;

    boolean batchMode = true;

    boolean offline;

    File workingDir;

    /** local repository for the invoked build - null for the Maven default */
    File localRepository;

    /** read-only local repositories consulted after the local repository (Maven 3.9+) - null for none */
    String localRepositoryTail;

    /** Maven installation used for the invoked build - null to let the invoker locate it */
    File mavenHome;

    /** additional user properties passed to the invoked build */
    Properties userProperties = new Properties();

    /** trailing output lines of the last invocation */
    private final Deque<String> outputTail = new ArrayDeque<>();

    public AbstractMavenCommand(List<String> goals)
    {
        this.goals = goals;
    }

    public List<String> getGoals()
    {
        return goals;
    }

    public void setGoals(List<String> goals)
    {
        this.goals = goals;
    }

    public boolean isDebug()
    {
        return debug;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    public boolean isBatchMode()
    {
        return batchMode;
    }

    public void setBatchMode(boolean batchMode)
    {
        this.batchMode = batchMode;
    }

    public boolean isOffline()
    {
        return offline;
    }

    public void setOffline(boolean offline)
    {
        this.offline = offline;
    }

    public File getWorkingDir()
    {
        return workingDir;
    }

    public void setWorkingDir(File workingDir)
    {
        this.workingDir = workingDir;
    }

    public File getLocalRepository()
    {
        return localRepository;
    }

    public void setLocalRepository(File localRepository)
    {
        this.localRepository = localRepository;
    }

    public String getLocalRepositoryTail()
    {
        return localRepositoryTail;
    }

    public void setLocalRepositoryTail(String localRepositoryTail)
    {
        this.localRepositoryTail = localRepositoryTail;
    }

    public File getMavenHome()
    {
        return mavenHome;
    }

    public void setMavenHome(File mavenHome)
    {
        this.mavenHome = mavenHome;
    }

    public Properties getUserProperties()
    {
        return userProperties;
    }

    public void setUserProperty(String key, String value)
    {
        this.userProperties.setProperty(key, value);
    }

    public Properties getCommandProperties()
    {
        return null;
    }

    /**
     * @return the trailing lines of output from the last invocation of this command
     */
    public String getOutputTail()
    {
        synchronized (outputTail)
        {
            return String.join(System.lineSeparator(), outputTail);
        }
    }

    InvocationResult executeCommand() throws MavenInvocationException
    {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(goals);
        request.setDebug(debug);
        request.setBatchMode(batchMode);
        request.setOffline(offline);
        request.setShowErrors(true);

        Properties props = new Properties();
        Properties commandProperties = getCommandProperties();
        if(commandProperties != null)
        {
            props.putAll(commandProperties);
        }
        props.putAll(userProperties);
        if(localRepositoryTail != null && !localRepositoryTail.isEmpty())
        {
            props.setProperty("maven.repo.local.tail", localRepositoryTail);
        }
        if(!props.isEmpty())
        {
            request.setProperties(props);
        }

        if(localRepository != null)
        {
            request.setLocalRepositoryDirectory(localRepository);
        }

        synchronized (outputTail)
        {
            outputTail.clear();
        }
        InvocationOutputHandler handler = line ->
        {
            System.out.println(line);
            synchronized (outputTail)
            {
                outputTail.addLast(line);
                if(outputTail.size() > OUTPUT_TAIL_LINES)
                {
                    outputTail.removeFirst();
                }
            }
        };
        request.setOutputHandler(handler);
        request.setErrorHandler(handler);

        Invoker invoker = new DefaultInvoker();
        if(mavenHome != null)
        {
            invoker.setMavenHome(mavenHome);
        }
        if(workingDir != null)
        {
            invoker.setWorkingDirectory(workingDir);
        }

        InvocationResult result = invoker.execute(request);
        workingDir = invoker.getWorkingDirectory();

        if(result.getExecutionException() != null)
        {
            throw new MavenInvocationException("Failed to invoke Maven goals " + goals
                + " in " + workingDir, result.getExecutionException());
        }
        if(result.getExitCode() != 0)
        {
            throw new MavenInvocationException("Maven goals " + goals + " in " + workingDir
                + " failed with exit code " + result.getExitCode() + ". Build output tail:"
                + System.lineSeparator() + getOutputTail());
        }

        return result;
    }

    @Override
    public RESULT execute() throws MavenInvocationException, CommandLineException
    {
        return (RESULT)executeCommand();
    }
}
