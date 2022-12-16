package org.ikasan.ootb.scheduler.agent.rest.cache;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.job.orchestration.model.context.ContextParameterInstanceImpl;
import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.Assert.*;

public class ContextInstanceCacheTest {

    @Test
    public void getContextParameter_should_return_null_values() {
        assertNull(ContextInstanceCache.getContextParameter(null, "contextParameterName"));
        assertNull(ContextInstanceCache.getContextParameter("contextInstanceId", null));
    }

    @Test
    public void getContextParameter_should_return_null_null_context_instance() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        assertNull(ContextInstanceCache.getContextParameter(contextInstanceId, "contextParameterName"));
    }

    @Test
    public void getContextParameter_should_return_null_null_context_parameters() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setId(contextInstanceId);

        ContextInstanceCache.instance().put(instance.getId(), instance);
        assertNull(ContextInstanceCache.getContextParameter(contextInstanceId, "contextParameterName"));
    }

    @Test
    public void getContextParameter_should_return_value() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setId(contextInstanceId);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("BusinessDate");
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        contextParameterInstance.setValue(date);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance.getId(), instance);
        assertEquals(date, (ContextInstanceCache.getContextParameter(contextInstanceId, contextParameterInstance.getName())));
    }

    @Test
    public void getContextParameter_should_return_null_with_context_parameters_null_name() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setId(contextInstanceId);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName(null);
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        contextParameterInstance.setValue(date);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance.getId(), instance);
        assertNull(ContextInstanceCache.getContextParameter(contextInstanceId, "BusinessDate"));
    }

    @Test
    public void getContextParameter_should_return_null_with_context_parameters_null_value() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setId(contextInstanceId);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("BusinessDate");
        contextParameterInstance.setValue(null);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance.getId(), instance);
        assertNull(ContextInstanceCache.getContextParameter(contextInstanceId, "BusinessDate"));
    }

    @Test
    public void existsInCache() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setId(contextInstanceId);
        ContextInstanceCache.instance().put(instance.getId(), instance);

        assertTrue(ContextInstanceCache.existsInCache(contextInstanceId));
        assertFalse(ContextInstanceCache.existsInCache(null));
        assertFalse(ContextInstanceCache.existsInCache(contextInstanceId + RandomStringUtils.randomAlphabetic(5)));

        ContextInstanceCache.instance().remove(null);
        assertTrue(ContextInstanceCache.existsInCache(contextInstanceId));

        ContextInstanceCache.instance().remove(contextInstanceId);
        assertFalse(ContextInstanceCache.existsInCache(contextInstanceId));
    }

    @Test
    public void doesNotExistInCache() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setId(contextInstanceId);
        ContextInstanceCache.instance().put(instance.getId(), instance);

        assertTrue(ContextInstanceCache.doesNotExistInCache(null));
        assertTrue(ContextInstanceCache.doesNotExistInCache(contextInstanceId + RandomStringUtils.randomAlphabetic(5)));
        assertTrue(ContextInstanceCache.existsInCache(contextInstanceId));
    }

    @Test
    public void getByContextId_and_remove() {
        String contextInstanceId1 = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance1 = new ContextInstanceImpl();
        instance1.setId(contextInstanceId1);
        ContextInstanceCache.instance().put(instance1.getId(), instance1);

        String contextInstanceId2 = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance2 = new ContextInstanceImpl();
        instance2.setId(contextInstanceId2);
        ContextInstanceCache.instance().put(instance2.getId(), instance2);

        assertNull(ContextInstanceCache.instance().getByCorrelationId(null));
        assertEquals(instance1, ContextInstanceCache.instance().getByCorrelationId(contextInstanceId1));
        assertEquals(instance2, ContextInstanceCache.instance().getByCorrelationId(contextInstanceId2));

        ContextInstanceCache.instance().remove(null);
        assertEquals(instance1, ContextInstanceCache.instance().getByCorrelationId(contextInstanceId1));
        assertEquals(instance2, ContextInstanceCache.instance().getByCorrelationId(contextInstanceId2));

        ContextInstanceCache.instance().remove(contextInstanceId1);
        assertNull(ContextInstanceCache.instance().getByCorrelationId(contextInstanceId1));
        assertEquals(instance2, ContextInstanceCache.instance().getByCorrelationId(contextInstanceId2));

        ContextInstanceCache.instance().remove(contextInstanceId2);
        assertNull(ContextInstanceCache.instance().getByCorrelationId(contextInstanceId1));
        assertNull(ContextInstanceCache.instance().getByCorrelationId(contextInstanceId2));
    }

    @Test
    public void put() {
        String contextInstanceId = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance1 = new ContextInstanceImpl();
        instance1.setId(contextInstanceId);

        ContextInstanceCache.instance().put(contextInstanceId, null);
        assertNull(ContextInstanceCache.instance().getByCorrelationId(contextInstanceId));

        ContextInstanceCache.instance().put(null, instance1);
        assertNull(ContextInstanceCache.instance().getByCorrelationId(contextInstanceId));

        ContextInstanceCache.instance().put(instance1.getId(), instance1);
        assertEquals(instance1, ContextInstanceCache.instance().getByCorrelationId(contextInstanceId));
    }

}