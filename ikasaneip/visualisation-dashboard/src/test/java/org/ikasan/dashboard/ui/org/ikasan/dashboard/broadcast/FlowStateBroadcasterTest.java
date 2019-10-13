package org.ikasan.dashboard.ui.org.ikasan.dashboard.broadcast;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FlowStateBroadcasterTest
{
    @Before
    public void setup()
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);
    }

    @Test
    public void test_broadcast() throws InterruptedException
    {
        MyConsumer myConsumer1 = new MyConsumer();
        MyConsumer myConsumer2 = new MyConsumer();
        MyConsumer myConsumer3 = new MyConsumer();
        MyConsumer myConsumer4 = new MyConsumer();
        MyConsumer myConsumer5 = new MyConsumer();

        Registration r1 = FlowStateBroadcaster.register(myConsumer1);
        Registration r2 = FlowStateBroadcaster.register(myConsumer2);
        Registration r3 = FlowStateBroadcaster.register(myConsumer3);
        Registration r4 = FlowStateBroadcaster.register(myConsumer4);
        Registration r5 = FlowStateBroadcaster.register(myConsumer5);

        FlowState flowState = new FlowState("moduleName", "flowState", "running");
        FlowStateBroadcaster.broadcast(flowState);

        Thread.sleep(20);

        Assertions.assertTrue(myConsumer1.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertEquals(flowState, myConsumer1.flowStates.get(0), "flow state equals");
        Assertions.assertEquals( new FlowState("moduleName", "flowState", "running"), myConsumer1.flowStates.get(0), "flow state equals");
        Assertions.assertTrue(myConsumer2.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer3.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer4.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer5.flowStates.size() == 1, "One flow state has been broadcast!");

        r1.remove();

        FlowStateBroadcaster.broadcast(new FlowState("moduleName", "flowState", "running"));

        Thread.sleep(20);

        Assertions.assertTrue(myConsumer1.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer2.flowStates.size() == 2, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer3.flowStates.size() == 2, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer4.flowStates.size() == 2, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer5.flowStates.size() == 2, "One flow state has been broadcast!");

        r2.remove();

        FlowStateBroadcaster.broadcast(new FlowState("moduleName", "flowState", "running"));

        Thread.sleep(20);

        Assertions.assertTrue(myConsumer1.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer2.flowStates.size() == 2, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer3.flowStates.size() == 3, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer4.flowStates.size() == 3, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer5.flowStates.size() == 3, "One flow state has been broadcast!");

        r3.remove();

        FlowStateBroadcaster.broadcast(new FlowState("moduleName", "flowState", "running"));

        Thread.sleep(20);

        Assertions.assertTrue(myConsumer1.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer2.flowStates.size() == 2, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer3.flowStates.size() == 3, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer4.flowStates.size() == 4, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer5.flowStates.size() == 4, "One flow state has been broadcast!");

        r4.remove();

        FlowStateBroadcaster.broadcast(new FlowState("moduleName", "flowState", "running"));

        Thread.sleep(20);

        Assertions.assertTrue(myConsumer1.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer2.flowStates.size() == 2, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer3.flowStates.size() == 3, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer4.flowStates.size() == 4, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer5.flowStates.size() == 5, "One flow state has been broadcast!");

        r5.remove();

        FlowStateBroadcaster.broadcast(new FlowState("moduleName", "flowState", "running"));

        Thread.sleep(20);

        Assertions.assertTrue(myConsumer1.flowStates.size() == 1, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer2.flowStates.size() == 2, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer3.flowStates.size() == 3, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer4.flowStates.size() == 4, "One flow state has been broadcast!");
        Assertions.assertTrue(myConsumer5.flowStates.size() == 5, "One flow state has been broadcast!");

    }

    private class MyConsumer implements Consumer<FlowState>
    {
        private List<FlowState> flowStates = new ArrayList<>();

        @Override
        public void accept(FlowState flowState)
        {
            flowStates.add(flowState);
        }

        @Override
        public Consumer<FlowState> andThen(Consumer<? super FlowState> after)
        {
            return null;
        }
    }
}
