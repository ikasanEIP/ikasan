package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.ikasan.ootb.scheduled.model.Outcome;
import org.ikasan.ootb.scheduler.agent.module.model.EnrichedContextualisedScheduledProcessEvent;
import org.ikasan.ootb.scheduler.agent.rest.dto.InternalEventDrivenJobDto;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JobStartingBrokerTest {

    @Test
    public void get_command_line_args()  {
        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        String cmd = "source $HOME/.bash_profile;\necho \"some_cmd(\\\"code = 'code'\\\");\" | do_some_something - | blah.sh -\t\t\nblah1.sh -c some_param -i\n";
        String[] commandLineArgs = jobStartingBroker.getCommandLineArgs(cmd);
        if (SystemUtils.OS_NAME.contains("Windows")) {
            Assert.assertEquals("cmd.exe", commandLineArgs[0]);
            Assert.assertEquals("/c", commandLineArgs[1]);
        } else {
            // unix flavour
            Assert.assertEquals("/bin/bash", commandLineArgs[0]);
            Assert.assertEquals("-c", commandLineArgs[1]);
        }

        Assert.assertEquals(cmd, commandLineArgs[2]);
    }

    @Test
    public void test_job_start_skipped_success() {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        enrichedContextualisedScheduledProcessEvent.setSkipped(true);
        InternalEventDrivenJobDto internalEventDrivenJobDto = new InternalEventDrivenJobDto();
        internalEventDrivenJobDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobDto.setCommandLine("pwd");
        }
        internalEventDrivenJobDto.setContextId("contextId");
        internalEventDrivenJobDto.setIdentifier("identifier");
        internalEventDrivenJobDto.setMinExecutionTime(1000L);
        internalEventDrivenJobDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobDto.setWorkingDirectory(".");
        enrichedContextualisedScheduledProcessEvent.setInternalEventDrivenJob(internalEventDrivenJobDto);

        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertEquals(0, enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getProcess());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertEquals(true, enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertEquals(true, enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertEquals(false, enrichedContextualisedScheduledProcessEvent.isDryRun());
    }

    @Test
    public void test_job_start_dry_run_success() {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        enrichedContextualisedScheduledProcessEvent.setSkipped(false);
        enrichedContextualisedScheduledProcessEvent.setDryRun(true);
        InternalEventDrivenJobDto internalEventDrivenJobDto = new InternalEventDrivenJobDto();
        internalEventDrivenJobDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobDto.setCommandLine("pwd");
        }
        internalEventDrivenJobDto.setContextId("contextId");
        internalEventDrivenJobDto.setIdentifier("identifier");
        internalEventDrivenJobDto.setMinExecutionTime(1000L);
        internalEventDrivenJobDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobDto.setWorkingDirectory(".");
        enrichedContextualisedScheduledProcessEvent.setInternalEventDrivenJob(internalEventDrivenJobDto);

        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertEquals(0, enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertNull(enrichedContextualisedScheduledProcessEvent.getProcess());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertEquals(true, enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertEquals(false, enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertEquals(true, enrichedContextualisedScheduledProcessEvent.isDryRun());
    }

    @Test
    public void test_job_start_success() {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        InternalEventDrivenJobDto internalEventDrivenJobDto = new InternalEventDrivenJobDto();
        internalEventDrivenJobDto.setAgentName("agent name");

        if (SystemUtils.OS_NAME.contains("Windows")) {
            internalEventDrivenJobDto.setCommandLine("java -version");
        }
        else {
            internalEventDrivenJobDto.setCommandLine("pwd");
        }
        internalEventDrivenJobDto.setContextId("contextId");
        internalEventDrivenJobDto.setIdentifier("identifier");
        internalEventDrivenJobDto.setMinExecutionTime(1000L);
        internalEventDrivenJobDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobDto.setWorkingDirectory(".");
        enrichedContextualisedScheduledProcessEvent.setInternalEventDrivenJob(internalEventDrivenJobDto);
        enrichedContextualisedScheduledProcessEvent.setResultError("err");
        enrichedContextualisedScheduledProcessEvent.setResultOutput("out");

        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertEquals(true, enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertEquals(false, enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertEquals(false, enrichedContextualisedScheduledProcessEvent.isDryRun());
    }

    @Test
    public void test_job_start_success_with_context_parameters() throws IOException {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        InternalEventDrivenJobDto internalEventDrivenJobDto = new InternalEventDrivenJobDto();
        internalEventDrivenJobDto.setAgentName("agent name");

        ContextParameterInstanceImpl contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("cmd");
        contextParameterInstance.setType("java.lang.String");
        contextParameterInstance.setValue("echo test");
        enrichedContextualisedScheduledProcessEvent.setContextParameters(List.of(contextParameterInstance));
        String cmd = "source $HOME/.some_profile \necho \"some_command(\\\"code = 'SOME_VAR'\\\");\"\\n | echo \"TEST\" | grep -i 'test' | echo \\\"END\\\" \\\t OF \\\"CMD\\\"";
        internalEventDrivenJobDto.setCommandLine(cmd);

        internalEventDrivenJobDto.setContextId("contextId");
        internalEventDrivenJobDto.setIdentifier("identifier");
        internalEventDrivenJobDto.setMinExecutionTime(1000L);
        internalEventDrivenJobDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobDto.setWorkingDirectory(".");
        enrichedContextualisedScheduledProcessEvent.setInternalEventDrivenJob(internalEventDrivenJobDto);
        enrichedContextualisedScheduledProcessEvent.setResultError("err");
        enrichedContextualisedScheduledProcessEvent.setResultOutput("out");

        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        enrichedContextualisedScheduledProcessEvent = jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);

        Assert.assertEquals(enrichedContextualisedScheduledProcessEvent.getProcess().pid(), enrichedContextualisedScheduledProcessEvent.getPid());
        Assert.assertEquals(Outcome.EXECUTION_INVOKED, enrichedContextualisedScheduledProcessEvent.getOutcome());
        Assert.assertEquals(true, enrichedContextualisedScheduledProcessEvent.isJobStarting());
        Assert.assertEquals(false, enrichedContextualisedScheduledProcessEvent.isSkipped());
        Assert.assertEquals(false, enrichedContextualisedScheduledProcessEvent.isDryRun());

        Assert.assertEquals("\"END\" \t OF \"CMD\"", loadDataFile(enrichedContextualisedScheduledProcessEvent.getResultOutput()).trim());
    }

    @Test(expected = EndpointException.class)
    public void test_exception_bad_command_line() throws IOException {
        EnrichedContextualisedScheduledProcessEvent enrichedContextualisedScheduledProcessEvent =
            new EnrichedContextualisedScheduledProcessEvent();
        InternalEventDrivenJobDto internalEventDrivenJobDto = new InternalEventDrivenJobDto();
        internalEventDrivenJobDto.setAgentName("agent name");

        ContextParameterInstanceImpl contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("cmd");
        contextParameterInstance.setType("java.lang.String");
        contextParameterInstance.setValue("echo test");
        enrichedContextualisedScheduledProcessEvent.setContextParameters(List.of(contextParameterInstance));

        internalEventDrivenJobDto.setContextId("contextId");
        internalEventDrivenJobDto.setIdentifier("identifier");
        internalEventDrivenJobDto.setMinExecutionTime(1000L);
        internalEventDrivenJobDto.setMaxExecutionTime(10000L);
        internalEventDrivenJobDto.setWorkingDirectory(".");
        enrichedContextualisedScheduledProcessEvent.setInternalEventDrivenJob(internalEventDrivenJobDto);
        enrichedContextualisedScheduledProcessEvent.setResultError("err");
        enrichedContextualisedScheduledProcessEvent.setResultOutput("out");

        JobStartingBroker jobStartingBroker = new JobStartingBroker();
        jobStartingBroker.invoke(enrichedContextualisedScheduledProcessEvent);
    }

    private class ContextParameterInstanceImpl implements ContextParameterInstance {
        private Object value;
        private String name;
        private String type;

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        public void setValue(Object value) {
            this.value = value;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String getType() {
            return this.type;
        }

        @Override
        public void setType(String type) {
            this.type = type;
        }
    }

    protected String loadDataFile(String fileName) throws IOException
    {
        String contentToSend = IOUtils.toString(loadDataFileStream(fileName), "UTF-8");

        return contentToSend;
    }

    protected InputStream loadDataFileStream(String fileName) throws IOException
    {
        return new FileInputStream(fileName);
    }
}
