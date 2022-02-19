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
import java.util.List;
import java.util.Properties;

/**
 * Abstract class representing the base for a Maven Command.
 * 
 * @author Ikasan Development Team
 */
public class AbstractMavenCommand<RESULT> implements MavenCommand<RESULT>
{
    /** Mvn goals to execute */
    List<String> goals;

    /** run mvn in debug or not - default no debug */
    boolean debug;

    /** run mvn in batch or interactive mode - default to run in batchmode */
    boolean batchMode = true;

    /** working directory within which to run the mvn command */
    File workingDir;

    /**
     * Constructor
     * @param goals
     */
    public AbstractMavenCommand(List<String> goals)
    {
        this.goals = goals;
    }

    /**
     * Getter for goals.
     * @return
     */
    public List<String> getGoals()
    {
        return goals;
    }

    /**
     * Setter for goals.
     * @param goals
     */
    public void setGoals(List<String> goals)
    {
        this.goals = goals;
    }

    /**
     * Getter for debug mode.
     * @return
     */
    public boolean isDebug()
    {
        return debug;
    }

    /**
     * Setter for debug mode.
     * @param debug
     */
    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * Setter for batch or interactive mode
     * @return
     */
    public boolean isBatchMode()
    {
        return batchMode;
    }

    /**
     * Getter for batch or interactive mode
     * @param batchMode
     */
    public void setBatchMode(boolean batchMode)
    {
        this.batchMode = batchMode;
    }

    /**
     * Getter for the working directory
     * @return
     */
    public File getWorkingDir()
    {
        return workingDir;
    }

    /**
     * Setter for the working directory
     * @param workingDir
     */
    public void setWorkingDir(File workingDir)
    {
        this.workingDir = workingDir;
    }

    /**
     * Getter for returning the mvn configuration as command properties
     * @return
     */
    public Properties getCommandProperties()
    {
        return null;
    }

    /**
     * Base execution implementation of the configured maven command.
     * @return
     * @throws MavenInvocationException
     */
    InvocationResult executeCommand() throws MavenInvocationException
    {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(goals);
        request.setDebug(debug);
        request.setBatchMode(batchMode);

        Properties props = getCommandProperties();
        if(props != null)
        {
            request.setProperties( getCommandProperties() );
        }

        Invoker invoker = new DefaultInvoker();
        if(workingDir != null)
        {
            invoker.setWorkingDirectory(workingDir);
        }

        InvocationResult result = invoker.execute(request);
        workingDir = invoker.getWorkingDirectory();
        return result;
    }

    @Override
    public RESULT execute() throws MavenInvocationException, CommandLineException
    {
        return (RESULT)executeCommand();
    }
}
