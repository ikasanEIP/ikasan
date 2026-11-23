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

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.utils.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Generates a project from an Ikasan archetype and builds it with a nested Maven invocation,
 * failing if either step does not succeed or the expected build outputs are missing.
 *
 * Intended to be driven from integration tests in the archetype modules. Configuration is taken
 * from system properties, normally supplied by the failsafe configuration of the archetype-it profile:
 * <ul>
 *     <li>archetype.groupId - archetype groupId (default org.ikasan)</li>
 *     <li>archetype.version - archetype version (required)</li>
 *     <li>it.workDir - directory the project is generated into (default target/it-projects)</li>
 *     <li>it.localRepository - local repository for the nested builds, holding the freshly built archetype</li>
 *     <li>it.localRepositoryTail - read-only repositories consulted after it.localRepository (Maven 3.9+)</li>
 *     <li>it.offline - run the nested builds offline (default false)</li>
 *     <li>it.goals - comma separated goals for building the generated project (default clean,verify)</li>
 *     <li>maven.home - Maven installation used for the nested builds</li>
 * </ul>
 *
 * @author Ikasan Development Team
 */
public class ArchetypeBuildVerifier
{
    static final String PROJECT_GROUP_ID = "com.sample";
    static final String PROJECT_VERSION = "1.0.0-SNAPSHOT";

    private final String archetypeArtifactId;

    private ArchetypeBuildVerifier(String archetypeArtifactId)
    {
        this.archetypeArtifactId = archetypeArtifactId;
    }

    public static ArchetypeBuildVerifier forArchetype(String archetypeArtifactId)
    {
        return new ArchetypeBuildVerifier(archetypeArtifactId);
    }

    /**
     * Generate a project from the archetype and build it.
     *
     * @param projectArtifactId artifactId of the generated project
     * @return the root directory of the generated project
     */
    public File generateAndBuild(String projectArtifactId)
        throws MavenInvocationException, CommandLineException, IOException
    {
        String archetypeVersion = System.getProperty("archetype.version");
        if(archetypeVersion == null || archetypeVersion.isEmpty())
        {
            throw new IllegalStateException("System property 'archetype.version' must be set - run with -Parchetype-it");
        }

        File workDir = new File(System.getProperty("it.workDir", "target" + File.separator + "it-projects"));
        File projectDir = new File(workDir, projectArtifactId);
        deleteRecursively(projectDir.toPath());
        Files.createDirectories(workDir.toPath());

        MavenArchetypeCommand generate = MavenFactory.getArchetypeCommand();
        configure(generate);
        generate.setArchetypeGroupId(System.getProperty("archetype.groupId", "org.ikasan"));
        generate.setArchetypeArtifactId(archetypeArtifactId);
        generate.setArchetypeVersion(archetypeVersion);
        generate.setGroupId(PROJECT_GROUP_ID);
        generate.setArtifactId(projectArtifactId);
        generate.setVersion(PROJECT_VERSION);
        generate.setWorkingDir(workDir);
        MavenProject project = generate.execute();
        assertGeneratedProject(projectDir);

        List<String> goals = Arrays.asList(System.getProperty("it.goals", "clean,verify").split(","));
        MavenGenericCommand build = MavenFactory.getGenericCommand(goals);
        configure(build);
        project.invoke(build);

        assertBuildOutputs(projectDir, projectArtifactId);
        return projectDir;
    }

    private void configure(AbstractMavenCommand<?> command)
    {
        command.setBatchMode(true);
        command.setOffline(Boolean.getBoolean("it.offline"));

        String localRepository = System.getProperty("it.localRepository");
        if(localRepository != null && !localRepository.isEmpty())
        {
            command.setLocalRepository(new File(localRepository));
        }
        command.setLocalRepositoryTail(System.getProperty("it.localRepositoryTail"));

        String mavenHome = System.getProperty("maven.home");
        if(mavenHome != null && !mavenHome.isEmpty())
        {
            command.setMavenHome(new File(mavenHome));
        }
    }

    /**
     * Fail fast with a clear message if generation did not produce a complete project, rather than
     * letting the build fail later with a misleading error.
     */
    private void assertGeneratedProject(File projectDir)
    {
        String[] expected = { "pom.xml", "jar/pom.xml", "jar/src/main", "distribution/pom.xml" };
        for(String path : expected)
        {
            File file = new File(projectDir, path.replace('/', File.separatorChar));
            if(!file.exists())
            {
                throw new AssertionError("Archetype " + archetypeArtifactId
                    + " did not generate a complete project - missing " + file);
            }
        }
    }

    private void assertBuildOutputs(File projectDir, String projectArtifactId) throws IOException
    {
        File jar = new File(projectDir, "jar" + File.separator + "target" + File.separator
            + projectArtifactId + "-" + PROJECT_VERSION + ".jar");
        if(!jar.isFile())
        {
            throw new AssertionError("Expected integration module jar was not built: " + jar);
        }

        Path distributionTarget = projectDir.toPath().resolve("distribution").resolve("target");
        boolean hasZip;
        try(Stream<Path> files = Files.exists(distributionTarget) ? Files.list(distributionTarget) : Stream.empty())
        {
            hasZip = files.anyMatch(p -> p.getFileName().toString().endsWith(".zip"));
        }
        if(!hasZip)
        {
            throw new AssertionError("Expected distribution zip was not built in " + distributionTarget);
        }
    }

    private static void deleteRecursively(Path path) throws IOException
    {
        if(!Files.exists(path))
        {
            return;
        }
        // delete deepest first; Files.delete fails loudly rather than silently leaving remnants,
        // which would otherwise put archetype:generate into partial mode against a stale project
        try(Stream<Path> walk = Files.walk(path))
        {
            for(Path p : (Iterable<Path>) walk.sorted(Comparator.reverseOrder())::iterator)
            {
                Files.delete(p);
            }
        }
    }
}
