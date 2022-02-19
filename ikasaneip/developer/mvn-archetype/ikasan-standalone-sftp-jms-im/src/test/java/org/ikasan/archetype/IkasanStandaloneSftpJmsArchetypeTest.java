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

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.apache.maven.shared.invoker.*;
import org.apache.maven.shared.utils.cli.CommandLineException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * This test class supports the <code>Ikasan Standalone SFTP to JMS Archetype</code> class.
 * 
 * @author Ikasan Development Team
 */
public class IkasanStandaloneSftpJmsArchetypeTest
{
    File projectLocation;
    File archetypeLocation;
    String archetypeArtefactId = "ikasan-standalone-sftp-jms-im-maven-plugin";
    String archetypeGroupId = "org.ikasan";
    String archetypeVersion;
    String archetypeId = "sftp-jms-im";

    @Before
    public void setup() throws IOException, URISyntaxException, XmlPullParserException
    {
        projectLocation = new File(Paths.get(getClass().getResource("/").toURI()).getParent().toString());
        archetypeLocation = new File(Paths.get(getClass().getResource("/").toURI()).getParent().toString() + File.separator + archetypeId);

        Model model = MavenFactory.getMavenModel("pom.xml");
        archetypeVersion = model.getVersion();

        if(archetypeLocation.exists())
        {
            FileUtils.deleteDirectory(archetypeLocation);
        }
    }

    /**
     * Test.
     */
    @Test
    public void test_successful_archetype_generate_and_test() throws URISyntaxException
    {
        MavenArchetypeCommand mavenArchetypeCommand = MavenFactory.getArchetypeCommand();
        mavenArchetypeCommand.setArchetypeArtifactId(archetypeArtefactId);
        mavenArchetypeCommand.setArchetypeGroupId(archetypeGroupId);
        mavenArchetypeCommand.setArchetypeVersion(archetypeVersion);
        mavenArchetypeCommand.setGroupId("com.sample");
        mavenArchetypeCommand.setArtifactId(archetypeId);
        mavenArchetypeCommand.setVersion("1.0.0-SNAPSHOT");
        mavenArchetypeCommand.setBatchMode(true);
        mavenArchetypeCommand.setDebug(false);
        mavenArchetypeCommand.setWorkingDir(projectLocation);

        try
        {
            MavenProject mavenProject = mavenArchetypeCommand.execute();
            MavenCommand clean = MavenFactory.getCleanCommand();
            clean.setBatchMode(true);
            mavenProject.invoke(clean);

            MavenCommand test = MavenFactory.getCleanCommand();
            test.setBatchMode(true);
            mavenProject.invoke(test);
        }
        catch(CommandLineException|MavenInvocationException e)
        {
            Assert.fail("Project failed to create from the Maven archetype - " + e.getMessage());
        }
    }
}
