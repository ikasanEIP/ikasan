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
package org.ikasan.cli.shell.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This test class supports the <code>ProcessUtils</code> class.
 * 
 * @author Ikasan Development Team
 */
class ProcessUtilsTest
{
    String testParentDir = "./tempTestParentDir";
    Path testParentDirPath = Paths.get(testParentDir);

    @AfterEach
    void teardown() throws IOException
    {
        if(Files.exists(testParentDirPath))
        {
            Files.walk(testParentDirPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }

    @Test
    void successful_getCommands_no_classpath_expansion()
    {
        String sampleProcess = "java -classpath cli/shell/target/test-classes org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";
        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-classpath", commands.get(1), "expected -classpath");
        assertEquals(commands.get(2), "cli/shell/target/test-classes" + ProcessUtils.CLASSPATH_SEPARATOR, "expected cli/shell/target/test-classes" + ProcessUtils.CLASSPATH_SEPARATOR);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
    }

    @Test
    void successful_getCommands_no_cp_expansion()
    {
        String sampleProcess = "java -cp cli/shell/target/test-classes org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";
        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-cp", commands.get(1), "expected -cp");
        assertEquals(commands.get(2), "cli/shell/target/test-classes" + ProcessUtils.CLASSPATH_SEPARATOR, "expected cli/shell/target/test-classes" + ProcessUtils.CLASSPATH_SEPARATOR);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
    }

    @Test
    void successful_getCommands_classpath_not_found()
    {
        String sampleProcess = "java -cp /notFound/h2*jar org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";
        assertThrows(RuntimeException.class, () -> {
            ProcessUtils.getCommands(sampleProcess);
        });
    }

    @Test
    void successful_getCommands_with_h2_classpath_expansion() throws IOException
    {
        Path testJar = Paths.get(testParentDir + "/lib/h2-1.2.3.jar");

        assertDoesNotThrow(() -> {
            Files.createDirectories(testJar.getParent());
            Files.createFile(testJar);
        }, "Failed to create test jar ");

        String sampleProcess = "java -classpath ./tempTestParentDir/lib/h2-*.jar org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";
        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-classpath", commands.get(1), "expected -classpath");
        assertEquals(commands.get(2), "./tempTestParentDir/lib/h2-1.2.3.jar" + ProcessUtils.CLASSPATH_SEPARATOR, "expected ./tempTestParentDir/lib/h2-1.2.3.jar" + ProcessUtils.CLASSPATH_SEPARATOR);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
    }

    @Test
    void successful_getCommands_with_h2_cp_expansion() throws IOException
    {
        Path testJar = Paths.get(testParentDir + "/lib/h2-1.2.3.jar");

        assertDoesNotThrow(() -> {
            Files.createDirectories(testJar.getParent());
            Files.createFile(testJar);
        }, "Failed to create test jar ");

        String sampleProcess = "java -cp ./tempTestParentDir/lib/h2-*.jar org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";
        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-cp", commands.get(1), "expected -cp");
        assertEquals(commands.get(2), "./tempTestParentDir/lib/h2-1.2.3.jar" + ProcessUtils.CLASSPATH_SEPARATOR, "expected ./tempTestParentDir/lib/h2-1.2.3.jar" + ProcessUtils.CLASSPATH_SEPARATOR);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
    }

    @Test
    void successful_getCommands_with_module_classpath_expansion() throws IOException
    {
        Path testJar = Paths.get(testParentDir + "/lib/module-name-1.2.3-SNAPSHOT.jar");

        assertDoesNotThrow(() -> {
            Files.createDirectories(testJar.getParent());
            Files.createFile(testJar);
        }, "Failed to create test jar ");

        String sampleProcess = "java -classpath ./tempTestParentDir/lib/module-name-*.jar org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";
        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-classpath", commands.get(1), "expected -classpath");
        assertEquals(commands.get(2), "./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar" + ProcessUtils.CLASSPATH_SEPARATOR, "expected ./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar" + ProcessUtils.CLASSPATH_SEPARATOR);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
    }

    @Test
    void successful_getCommands_with_module_cp_expansion() throws IOException
    {
        Path testJar = Paths.get(testParentDir + "/lib/module-name-1.2.3-SNAPSHOT.jar");

        assertDoesNotThrow(() -> {
            Files.createDirectories(testJar.getParent());
            Files.createFile(testJar);
        }, "Failed to create test jar ");

        String sampleProcess = "java -cp ./tempTestParentDir/lib/module-name-*.jar org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";
        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-cp", commands.get(1), "expected -cp");
        assertEquals(commands.get(2), "./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar" + ProcessUtils.CLASSPATH_SEPARATOR, "expected ./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar" + ProcessUtils.CLASSPATH_SEPARATOR);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
    }

    @Test
    void successful_getCommands_with_multi_classpath_expansion() throws IOException
    {
        Path testJar1 = Paths.get(testParentDir + "/lib/module-name-1.2.3-SNAPSHOT.jar");
        Path testJar2 = Paths.get(testParentDir + "/somewhere/else/h2-100.200.300-SNAPSHOT.jar");

        assertDoesNotThrow(() -> {
            Files.createDirectories(testJar1.getParent());
            Files.createFile(testJar1);
            Files.createDirectories(testJar2.getParent());
            Files.createFile(testJar2);
        }, "Failed to create test jar ");

        String sampleProcess = "java -classpath ./tempTestParentDir/somewhere/else/h2*.jar"
            + ProcessUtils.CLASSPATH_SEPARATOR
            + "./tempTestParentDir/lib/module-name-*.jar org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server";

        String expectedExpandedClasspath = "./tempTestParentDir/somewhere/else/h2-100.200.300-SNAPSHOT.jar"
            + ProcessUtils.CLASSPATH_SEPARATOR
            + "./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar"
            + ProcessUtils.CLASSPATH_SEPARATOR;

        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-classpath", commands.get(1), "expected -classpath");
        assertEquals(commands.get(2), expectedExpandedClasspath, "expected " + expectedExpandedClasspath);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
    }

    @Test
    void successful_getCommands_with_multi_cp_expansion() throws IOException
    {
        Path testJar1 = Paths.get(testParentDir + "/lib/module-name-1.2.3-SNAPSHOT.jar");
        Path testJar2 = Paths.get(testParentDir + "/somewhere/else/h2-100.200.300-SNAPSHOT.jar");

        assertDoesNotThrow(() -> {
            Files.createDirectories(testJar1.getParent());
            Files.createFile(testJar1);
            Files.createDirectories(testJar2.getParent());
            Files.createFile(testJar2);
        }, "Failed to create test jar ");

        String sampleProcess = "java -cp ./tempTestParentDir/somewhere/else/h2*.jar"
            + ProcessUtils.CLASSPATH_SEPARATOR
            + "./tempTestParentDir/lib/module-name-*.jar org.ikasan.cli.sample.process.SampleProcess -Dmodule.name=sampleProcess -DfakeH2Signature=org.h2.tools.Server -jar ./tempTestParentDir/lib/module-name-*.jar";

        String expectedExpandedClasspath = "./tempTestParentDir/somewhere/else/h2-100.200.300-SNAPSHOT.jar"
            + ProcessUtils.CLASSPATH_SEPARATOR
            + "./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar"
            + ProcessUtils.CLASSPATH_SEPARATOR;

        List<String> commands = ProcessUtils.getCommands(sampleProcess);

        assertEquals("java", commands.get(0), "expected java");
        assertEquals("-cp", commands.get(1), "expected -cp");
        assertEquals(commands.get(2), expectedExpandedClasspath, "expected " + expectedExpandedClasspath);
        assertEquals("org.ikasan.cli.sample.process.SampleProcess", commands.get(3), "expected org.ikasan.cli.sample.process.SampleProcess");
        assertEquals("-Dmodule.name=sampleProcess", commands.get(4), "expected -Dmodule.name=sampleProcess");
        assertEquals("-DfakeH2Signature=org.h2.tools.Server", commands.get(5), "expected -DfakeH2Signature=org.h2.tools.Server");
        assertEquals("-jar", commands.get(6), "expected -jar");
        assertEquals("./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar", commands.get(7), "expected ./tempTestParentDir/lib/module-name-1.2.3-SNAPSHOT.jar");
    }
}