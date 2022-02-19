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

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maven Command Factory to simplify the client interaction.
 * 
 * @author Ikasan Development Team
 */
public class MavenFactory
{
    /**
     * Get an instance of a Maven project.0n77
     * @param workingDir
     * @return
     */
    public static MavenProject getMavenProject(File workingDir)
    {
        return new MavenProjectDefaultImpl(workingDir);
    }

    /**
     * Create an instance of the Maven Archetype command.
     * @return
     */
    public static MavenArchetypeCommand getArchetypeCommand()
    {
        return new MavenArchetypeCommand();
    }

    /**
     * Create an instance of the Maven command that can be generically configured for any goal(s).
     * @param goals
     * @return
     */
    public static MavenGenericCommand getGenericCommand(List<String> goals)
    {
        return new MavenGenericCommand(goals);
    }

    /**
     * Create an instance of the Maven command with a clean goal.
     * @return
     */
    public static MavenGenericCommand getCleanCommand()
    {
        return new MavenGenericCommand(Collections.singletonList("clean"));
    }

    /**
     * Create an instance of the Maven command with a build goal.
     * @return
     */
    public static MavenGenericCommand getBuildCommand()
    {
        List<String> goals = new ArrayList<String>();
        goals.add("build");
        return new MavenGenericCommand(goals);
    }

    /**
     * Create an instance of the Maven command with a test goal.
     * @return
     */
    public static MavenGenericCommand getTestCommand()
    {
        List<String> goals = new ArrayList<String>();
        goals.add("test");
        return new MavenGenericCommand(goals);
    }

    public static Model getMavenModel(String path) throws IOException, XmlPullParserException
    {
        MavenXpp3Reader reader = new MavenXpp3Reader();
        if ((new File(path)).exists())
        {
            return reader.read(new FileReader(path));
        }

        throw new IOException(path + " does not exist!");
    }
}
