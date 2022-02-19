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

import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.utils.cli.CommandLineException;

import java.io.File;
import java.util.*;

/**
 * Maven Archetype Command implementation.
 * 
 * @author Ikasan Development Team
 */
public class MavenArchetypeCommand extends AbstractMavenCommand implements MavenCommand
{
    String archetypeGroupId = "org.ikasan";
    String archetypeArtifactId;
    String archetypeVersion;
    String groupId;
    String artifactId;
    String version;
    String sourceFlowName;
    String targetFlowName;

    public MavenArchetypeCommand()
    {
        super(Collections.singletonList("archetype:generate"));
    }

    public String getArchetypeGroupId()
    {
        return archetypeGroupId;
    }

    public void setArchetypeGroupId(String archetypeGroupId)
    {
        this.archetypeGroupId = archetypeGroupId;
    }

    public String getArchetypeArtifactId()
    {
        return archetypeArtifactId;
    }

    public void setArchetypeArtifactId(String archetypeArtifactId)
    {
        this.archetypeArtifactId = archetypeArtifactId;
    }

    public String getArchetypeVersion()
    {
        return archetypeVersion;
    }

    public void setArchetypeVersion(String archetypeVersion)
    {
        this.archetypeVersion = archetypeVersion;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public void setGroupId(String groupId)
    {
        this.groupId = groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public void setArtifactId(String artifactId)
    {
        this.artifactId = artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getSourceFlowName()
    {
        return sourceFlowName;
    }

    public void setSourceFlowName(String sourceFlowName)
    {
        this.sourceFlowName = sourceFlowName;
    }

    public String getTargetFlowName()
    {
        return targetFlowName;
    }

    public void setTargetFlowName(String targetFlowName)
    {
        this.targetFlowName = targetFlowName;
    }

    @Override
    public Properties getCommandProperties()
    {
        Properties params = new Properties();
        if(archetypeArtifactId != null)
        {
            params.setProperty("archetypeArtifactId", archetypeArtifactId);
        }
        if(archetypeGroupId != null)
        {
            params.setProperty("archetypeGroupId", archetypeGroupId);
        }
        if(archetypeVersion != null)
        {
            params.setProperty("archetypeVersion", archetypeVersion);
        }
        if(groupId != null)
        {
            params.setProperty("groupId", groupId);
        }
        if(artifactId != null)
        {
            params.setProperty("artifactId", artifactId);
        }
        if(version != null)
        {
            params.setProperty("version", version);
        }
        if(sourceFlowName != null)
        {
            params.setProperty("sourceFlowName", sourceFlowName);
        }
        if(targetFlowName != null)
        {
            params.setProperty("targetFlowName", targetFlowName);
        }

        return params;
    }

    @Override
    public MavenProject execute() throws MavenInvocationException, CommandLineException
    {
        InvocationResult result = super.executeCommand();
        if(result.getExitCode() != 0)
        {
            throw result.getExecutionException();
        }

        return MavenFactory.getMavenProject( new File(this.workingDir + File.separator + artifactId)  );
    }
}
