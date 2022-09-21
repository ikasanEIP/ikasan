package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.component.endpoint.bigqueue.builder.BigQueueMessageBuilder;
import org.ikasan.component.endpoint.bigqueue.message.BigQueueMessageImpl;
import org.ikasan.ootb.scheduled.model.ContextualisedScheduledProcessEventImpl;
import org.ikasan.spec.bigqueue.message.BigQueueMessage;
import org.ikasan.spec.scheduled.event.model.ContextualisedScheduledProcessEvent;
import org.ikasan.spec.scheduled.event.model.ScheduledProcessEvent;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ScheduledProcessEventToBigQueueMessageSerialiserTest {

    @Test
    public void serialise_should_return_big_queue_message_as_bytes() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ScheduledProcessEventToBigQueueMessageSerialiser serialiser = new ScheduledProcessEventToBigQueueMessageSerialiser();

        ScheduledProcessEvent event = getEvent();
        byte[] serialised = serialiser.serialise(event);

        BigQueueMessage bigQueueMessage = mapper.readValue(new String(serialised), BigQueueMessageImpl.class);

        assertNotNull(bigQueueMessage.getMessageId());
        // make sure with the last x seconds
        assertTrue(bigQueueMessage.getCreatedTime() > System.currentTimeMillis() - 5000);
        Map<String, String> messageProperties = bigQueueMessage.getMessageProperties();
        assertNotNull(messageProperties);
        assertEquals("ContextIdName", messageProperties.get("contextName"));
        assertEquals("ContextInstanceIdForContext", messageProperties.get("contextInstanceId"));

        assertNotNull(bigQueueMessage.getMessage());
        String actual = mapper.writeValueAsString(bigQueueMessage.getMessage());
        String expected = mapper.writeValueAsString(event);
        assertEquals(expected, actual);
    }

    @Test
    public void deserialise_should_return_scheduled_process_event() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ScheduledProcessEventToBigQueueMessageSerialiser serialiser = new ScheduledProcessEventToBigQueueMessageSerialiser();

        ScheduledProcessEvent event = getEvent();
        BigQueueMessage bigQueueMessage = new BigQueueMessageBuilder().withMessage(event).build();
        byte[] bytes = mapper.writeValueAsBytes(bigQueueMessage);

        ContextualisedScheduledProcessEvent scheduledProcessEvent = (ContextualisedScheduledProcessEvent) serialiser.deserialise(bytes);
        assertEquals("ContextIdName", scheduledProcessEvent.getContextName());
        assertEquals("ContextInstanceIdForContext", scheduledProcessEvent.getContextInstanceId());
        assertEquals(List.of("child1", "child2"), scheduledProcessEvent.getChildContextNames());
        assertEquals("agentName", scheduledProcessEvent.getAgentName());
        assertEquals("hostname", scheduledProcessEvent.getAgentHostname());
        assertEquals("commandLine", scheduledProcessEvent.getCommandLine());
        assertEquals(1000L, scheduledProcessEvent.getFireTime());
        assertEquals(2000L, scheduledProcessEvent.getNextFireTime());
        assertEquals("jodDescription", scheduledProcessEvent.getJobDescription());
        assertEquals("jobGroup", scheduledProcessEvent.getJobGroup());
        assertEquals("jobName", scheduledProcessEvent.getJobName());
        assertEquals(111111, scheduledProcessEvent.getPid());
        assertEquals("output", scheduledProcessEvent.getResultOutput());
        assertEquals(1, scheduledProcessEvent.getReturnCode());
        assertEquals(3000L, scheduledProcessEvent.getCompletionTime());
        assertEquals("user", scheduledProcessEvent.getUser());
    }

    private ScheduledProcessEvent getEvent() {
        ContextualisedScheduledProcessEvent scheduledProcessEvent = new ContextualisedScheduledProcessEventImpl();
        scheduledProcessEvent.setContextName("ContextIdName");
        scheduledProcessEvent.setContextInstanceId("ContextInstanceIdForContext");
        scheduledProcessEvent.setChildContextNames(List.of("child1", "child2"));
        scheduledProcessEvent.setAgentName("agentName");
        scheduledProcessEvent.setAgentHostname("hostname");
        scheduledProcessEvent.setCommandLine("commandLine");
        scheduledProcessEvent.setFireTime(1000L);
        scheduledProcessEvent.setNextFireTime(2000L);
        scheduledProcessEvent.setJobDescription("jodDescription");
        scheduledProcessEvent.setJobGroup("jobGroup");
        scheduledProcessEvent.setJobName("jobName");
        scheduledProcessEvent.setPid(111111);
        scheduledProcessEvent.setResultOutput("output");
        scheduledProcessEvent.setReturnCode(1);
        scheduledProcessEvent.setSuccessful(false);
        scheduledProcessEvent.setCompletionTime(3000L);
        scheduledProcessEvent.setUser("user");

        return scheduledProcessEvent;
    }

}