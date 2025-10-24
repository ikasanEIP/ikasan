package org.ikasan.ootb.scheduler.agent.module.service;

import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextInstanceCache;
import org.ikasan.spec.harvest.HarvestService;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.InstanceStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ContextInstanceIdentifierProvisionServiceImplTest {
    @InjectMocks
    private ContextInstanceIdentifierProvisionServiceImpl service;

    private static final String ROOT_PLAN_NAME = "plan1";
    private static final String ROOT_PLAN_CONTEXT_INTANCE_ID = "instance123";
    private ContextInstance contextInstance;

    @Before
    public void setup() {
        contextInstance = new ContextInstanceImpl();
        contextInstance.setName(ROOT_PLAN_NAME);
        contextInstance.setId(ROOT_PLAN_CONTEXT_INTANCE_ID);
        contextInstance.setStatus(InstanceStatus.RUNNING);
        ContextInstanceCache.instance().removeAll();
    }

    @Test
    public void test_provision_instance() {
        service.provision(contextInstance);

        assertEquals(1, ContextInstanceCache.getCorrelationIds().size());
        assertEquals(contextInstance.getId(), ContextInstanceCache.instance().getByCorrelationId(contextInstance.getId()).getId());
    }

    @Test
    public void test_reset_provision_instances() {
        ContextInstance contextInstance1 = new ContextInstanceImpl();
        contextInstance1.setId("id1");
        contextInstance1.setStatus(InstanceStatus.RUNNING);
        ContextInstance contextInstance2 = new ContextInstanceImpl();
        contextInstance2.setId("id2");
        contextInstance2.setStatus(InstanceStatus.RUNNING);
        ContextInstance contextInstance3 = new ContextInstanceImpl();
        contextInstance3.setId("id3");
        contextInstance3.setStatus(InstanceStatus.PREPARED);

        HashMap<String, ContextInstance> contextInstanceMap = new HashMap<>();
        contextInstanceMap.put(contextInstance1.getId(), contextInstance1);
        contextInstanceMap.put(contextInstance2.getId(), contextInstance2);
        contextInstanceMap.put(contextInstance3.getId(), contextInstance3);

        service.reset(contextInstanceMap);

        assertEquals(2, ContextInstanceCache.getCorrelationIds().size());
        assertEquals(contextInstance1.getId(), ContextInstanceCache.instance().getByCorrelationId(contextInstance1.getId()).getId());
        assertEquals(contextInstance2.getId(), ContextInstanceCache.instance().getByCorrelationId(contextInstance2.getId()).getId());
    }

    @Test
    public void test_remove_all() {
        service.removeAll();

        assertEquals(0, ContextInstanceCache.getCorrelationIds().size());
    }

    @Test
    public void test_remove_success() {
        service.provision(contextInstance);
        service.remove(contextInstance.getId());

        assertEquals(0, ContextInstanceCache.getCorrelationIds().size());
    }
}