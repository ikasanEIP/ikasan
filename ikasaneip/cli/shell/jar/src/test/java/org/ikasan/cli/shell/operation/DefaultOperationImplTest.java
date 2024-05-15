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
package org.ikasan.cli.shell.operation;

import org.apache.commons.io.FileUtils;
import org.ikasan.cli.shell.operation.model.ProcessType;
import org.ikasan.cli.shell.operation.service.PersistenceService;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This test class supports the <code>efaultOperationImpl</code> class.
 * 
 * @author Ikasan Development Team
 */
class DefaultOperationImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    List<Process> processes = new ArrayList<Process>();

    /** Mock ProcessType */
    final ProcessType processType = mockery.mock(ProcessType.class, "mockProcessType");

    /** Mock IkasanProcess */
    final PersistenceService persistenceService = mockery.mock(PersistenceService.class, "mockPersistenceService");

    @Test
    void successful_start_with_persistence() throws IOException
    {
        List<String> commands = new ArrayList<String>();
        commands.add("java");
        commands.add("-classpath");
        commands.add("cli/shell/target/test-classes");
        commands.add("org.ikasan.cli.sample.process.SampleProcess");
        commands.add("-Dmodule.name=sampleProcess0");

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processType).isPersist();
                will(returnValue(true));

                exactly(1).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(processType).getOutputLog();
                will(returnValue(null));

                exactly(1).of(processType).getErrorLog();
                will(returnValue(null));

                exactly(1).of(persistenceService).persist(with(any(String.class)), with(any(String.class)), with(any(Process.class)));
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        Process process = operation.start(processType, commands, "sampleProcess");
        processes.add(process);
        Assert.assertNotNull(process);
        mockery.assertIsSatisfied();
    }

    @Test
    void successful_start_without_persistence() throws IOException
    {
        List<String> commands = new ArrayList<String>();
        commands.add("java");
        commands.add("-classpath");
        commands.add("cli/shell/target/test-classes");
        commands.add("org.ikasan.cli.sample.process.SampleProcess");
        commands.add("-Dmodule.name=sampleProcess6");

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processType).isPersist();
                will(returnValue(false));

                exactly(1).of(processType).getOutputLog();
                will(returnValue(null));

                exactly(1).of(processType).getErrorLog();
                will(returnValue(null));
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        Process process = operation.start(processType, commands, "sampleProcess");
        processes.add(process);
        Assert.assertNotNull(process);
        mockery.assertIsSatisfied();
    }

    @Test
    void successful_start_with_failed_persistence() throws IOException
    {
        List<String> commands = new ArrayList<String>();
        commands.add("java");
        commands.add("-classpath");
        commands.add("cli/shell/target/test-classes");
        commands.add("org.ikasan.cli.sample.process.SampleProcess");
        commands.add("-Dmodule.name=sampleProcess");

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processType).isPersist();
                will(returnValue(true));

                exactly(1).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(processType).getOutputLog();
                will(returnValue(null));

                exactly(1).of(processType).getErrorLog();
                will(returnValue(null));

                exactly(1).of(persistenceService).persist(with(any(String.class)), with(any(String.class)), with(any(Process.class)));
                will(throwException(new RuntimeException("failed to persist")));
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        Process process = operation.start(processType, commands, "sampleProcess");
        processes.add(process);
        Assert.assertNotNull(process);
        mockery.assertIsSatisfied();
    }

    @Test
    void successful_isRunning_persisted_process_found_correlates_with_running_process() throws IOException, InterruptedException, ExecutionException {
        ProcessBuilder processBuilder
            = new ProcessBuilder("java", "-Dmodule.name=name"
            ,"-cp","./target/test-classes","org.ikasan.cli.sample.process.SampleProcess");

        ProcessHandle javaProcess = processBuilder.start().toHandle();

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processType).getCommandSignature();
                will(returnValue("org.ikasan.cli.sample.process.SampleProcess"));

                exactly(1).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(persistenceService).find("processTypeName", "name");
                will(returnValue(javaProcess));
            }
        });


        Operation operation = new DefaultOperationImpl(persistenceService);
        List<ProcessHandle> processHandles = operation.getProcessHandles(processType, "name"
            , javaProcess.info().user().get());
        Assert.assertTrue(processHandles.get(0).isAlive());
        mockery.assertIsSatisfied();

        javaProcess.destroyForcibly();
    }

    @Test
    void successful_isRunning_persisted_process_found_does_not_correlate_with_running_process_bad_process_name() throws IOException, InterruptedException, ExecutionException {
        ProcessBuilder processBuilder
            = new ProcessBuilder("java", "-Dmodule.name=differentProcessName"
            ,"-cp","./target/test-classes","org.ikasan.cli.sample.process.SampleProcess");

        ProcessHandle javaProcess = processBuilder.start().toHandle();

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processType).getCommandSignature();
                will(returnValue("org.ikasan.cli.sample.process.SampleProcess"));

                exactly(2).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(persistenceService).find("processTypeName", "name");
                will(returnValue(javaProcess));

                exactly(1).of(persistenceService).remove("processTypeName", "name");
            }
        });


        Operation operation = new DefaultOperationImpl(persistenceService);
        List<ProcessHandle> processHandles = operation.getProcessHandles(processType, "name"
            , javaProcess.info().user().get());
        Assert.assertTrue(processHandles.size() == 0);
        mockery.assertIsSatisfied();

        javaProcess.destroyForcibly();
    }

    @Test
    void successful_isRunning_persisted_process_found_does_not_correlate_with_running_process_bad_command_signature() throws IOException, InterruptedException, ExecutionException {
        ProcessBuilder processBuilder
            = new ProcessBuilder("java", "-Dmodule.name=name"
            ,"-cp","./target/test-classes","org.ikasan.cli.sample.process.SampleProcess");

        ProcessHandle javaProcess = processBuilder.start().toHandle();

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processType).getCommandSignature();
                will(returnValue("bad command signature"));

                exactly(2).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(persistenceService).find("processTypeName", "name");
                will(returnValue(javaProcess));

                exactly(1).of(persistenceService).remove("processTypeName", "name");
            }
        });


        Operation operation = new DefaultOperationImpl(persistenceService);
        List<ProcessHandle> processHandles = operation.getProcessHandles(processType, "name"
            , javaProcess.info().user().get());
        Assert.assertTrue(processHandles.size() == 0);
        mockery.assertIsSatisfied();

        javaProcess.destroyForcibly();
    }

    @Test
    void successful_getProcessHandles_no_persistence_without_user_not_found() throws IOException
    {
        List<String> commands = new ArrayList<String>();
        commands.add("java");
        commands.add("-classpath");
        commands.add("cli/shell/target/test-classes");
        commands.add("org.ikasan.cli.sample.process.SampleProcess");
        commands.add("-Dmodule.name=sampleProcess");
        commands.add("-DcommandSignature=commandSignature");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();
        processes.add(process);

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(persistenceService).find("processTypeName", "sampleProcess");
                will(returnValue(null));

                exactly(1).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(processType).getCommandSignature();
                will(returnValue(null));
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        List<ProcessHandle> processHandles = operation.getProcessHandles(processType, "sampleProcess", "username");
        Assert.assertTrue(processHandles.size() == 0);
        mockery.assertIsSatisfied();
    }

    @Test
    void successful_getProcessHandles_with_user_not_found() throws IOException
    {
        List<String> commands = new ArrayList<String>();
        commands.add("java");
        commands.add("-classpath");
        commands.add("cli/shell/target/test-classes");
        commands.add("org.ikasan.cli.sample.process.SampleProcess");
        commands.add("-Dmodule.name=sampleProcess");
        commands.add("-DcommandSignature=commandSignature");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();
        processes.add(process);

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(persistenceService).find("processTypeName", "sampleProcess");
                will(returnValue(null));

                exactly(1).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(processType).getCommandSignature();
                will(returnValue(null));
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        List<ProcessHandle> processHandles = operation.getProcessHandles(processType, "sampleProcess", "username");
        Assert.assertTrue(processHandles.size() == 0);
        mockery.assertIsSatisfied();
    }

    @Test
    void failed_stop_no_process_found() throws IOException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(persistenceService).find("processTypeName", "name");
                will(returnValue(null));

                exactly(1).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(processType).getCommandSignature();
                will(returnValue("commandSignature"));
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        Assertions.assertThrows(IOException.class,
            () -> operation.stop(processType, "name", "username"));

        mockery.assertIsSatisfied();
    }

    @Test
    void successful_stop_process_found() throws IOException
    {
        List<String> commands = new ArrayList<String>();
        commands.add("java");
        commands.add("-classpath");
        commands.add("cli/shell/target/test-classes");
        commands.add("org.ikasan.cli.sample.process.SampleProcess");
        commands.add("-Dmodule.name=sampleProcess");
        commands.add("-DcommandSignature=commandSignature");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();
        processes.add(process);

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(persistenceService).find("processTypeName", "sampleProcess");
                will(returnValue(null));

                exactly(2).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(processType).getCommandSignature();
                will(returnValue(null));

                exactly(1).of(persistenceService).remove("processTypeName", "sampleProcess");
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        operation.stop(processType, "sampleProcess", null);
        mockery.assertIsSatisfied();
    }

    @Test
    void failed_kill_no_process_found() throws IOException
    {
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(persistenceService).find("processTypeName", "name");
                will(returnValue(null));

                exactly(1).of(processType).getCommandSignature();
                will(returnValue("commandSignature"));
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);

        Assertions.assertThrows(IOException.class,
            () -> operation.kill(processType, "name", "username"));

        mockery.assertIsSatisfied();
    }

    @Test
    void successful_kill_process_found() throws IOException
    {
        List<String> commands = new ArrayList<String>();
        commands.add("java");
        commands.add("-classpath");
        commands.add("cli/shell/target/test-classes");
        commands.add("org.ikasan.cli.sample.process.SampleProcess");
        commands.add("-Dmodule.name=sampleProcess");

        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();
        processes.add(process);

        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(persistenceService).find("processTypeName", "sampleProcess");
                will(returnValue(null));

                exactly(2).of(processType).getName();
                will(returnValue("processTypeName"));

                exactly(1).of(processType).getCommandSignature();
                will(returnValue(null));

                exactly(1).of(persistenceService).remove("processTypeName", "sampleProcess");
            }
        });

        Operation operation = new DefaultOperationImpl(persistenceService);
        operation.kill(processType, "sampleProcess", null);
        mockery.assertIsSatisfied();
    }

    @AfterEach
    void teardown() throws IOException
    {
        for(Process process:processes)
        {
            if(process.isAlive())
            {
                process.destroyForcibly();
            }
        }

        FileUtils.deleteDirectory(new File("./logs"));
    }

}

