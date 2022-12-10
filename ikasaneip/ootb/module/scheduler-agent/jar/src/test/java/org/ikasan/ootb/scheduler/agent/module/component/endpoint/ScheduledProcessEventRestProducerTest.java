package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.ScheduledProcessEventRestProducer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledProcessEventRestProducerTest {

    @Mock
    DashboardRestService dashboardRestService;

    @Test(expected = IllegalArgumentException.class)
    public void test_exception_constructor_null_dashboard_rest_service() {
        new ScheduledProcessEventRestProducer(null);
    }

    @Test
    public void test_invoke_success() {
        when(dashboardRestService.publish(anyString())).thenReturn(true);

        ScheduledProcessEventRestProducer producer = new ScheduledProcessEventRestProducer(dashboardRestService);

        producer.invoke("payload");
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_failure() {
        when(dashboardRestService.publish(anyString())).thenReturn(false);

        ScheduledProcessEventRestProducer producer = new ScheduledProcessEventRestProducer(dashboardRestService);

        producer.invoke("payload");
    }

    @Test(expected = EndpointException.class)
    public void test_invoke_runtime_exception() {
        when(dashboardRestService.publish(anyString())).thenThrow(new RuntimeException("error!"));

        ScheduledProcessEventRestProducer producer = new ScheduledProcessEventRestProducer(dashboardRestService);

        producer.invoke("payload");
    }
}
