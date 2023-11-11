package org.ikasan.ootb.scheduler.agent.module.component.endpoint;

import org.ikasan.ootb.scheduler.agent.module.component.endpoint.producer.ScheduledProcessEventRestProducer;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.dashboard.DashboardRestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledProcessEventRestProducerTest {

    @Mock
    DashboardRestService dashboardRestService;

    @Test
    void test_exception_constructor_null_dashboard_rest_service() {
        assertThrows(IllegalArgumentException.class, () -> {
            new ScheduledProcessEventRestProducer(null);
        });
    }

    @Test
    void test_invoke_success() {
        when(dashboardRestService.publish(anyString())).thenReturn(true);

        ScheduledProcessEventRestProducer producer = new ScheduledProcessEventRestProducer(dashboardRestService);

        producer.invoke("payload");
    }

    @Test
    void test_invoke_failure() {
        assertThrows(EndpointException.class, () -> {
            when(dashboardRestService.publish(anyString())).thenReturn(false);

            ScheduledProcessEventRestProducer producer = new ScheduledProcessEventRestProducer(dashboardRestService);

            producer.invoke("payload");
        });
    }

    @Test
    void test_invoke_runtime_exception() {
        assertThrows(EndpointException.class, () -> {
            when(dashboardRestService.publish(anyString())).thenThrow(new RuntimeException("error!"));

            ScheduledProcessEventRestProducer producer = new ScheduledProcessEventRestProducer(dashboardRestService);

            producer.invoke("payload");
        });
    }
}
