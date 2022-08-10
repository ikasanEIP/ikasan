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
        assertNull(ContextInstanceCache.getContextParameter("contextName", null));
    }

    @Test
    public void getContextParameter_should_return_null_null_context_instance() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        assertNull(ContextInstanceCache.getContextParameter(contextName, "contextParameterName"));
    }

    @Test
    public void getContextParameter_should_return_null_null_context_parameters() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);

        ContextInstanceCache.instance().put(instance.getName(), instance);
        assertNull(ContextInstanceCache.getContextParameter(contextName, "contextParameterName"));
    }

    @Test
    public void getContextParameter_should_return_value() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("BusinessDate");
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        contextParameterInstance.setValue(date);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance.getName(), instance);
        assertEquals(date, (ContextInstanceCache.getContextParameter(contextName, contextParameterInstance.getName())));
    }

    @Test
    public void getContextParameter_should_return_null_with_context_parameters_null_name() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName(null);
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        contextParameterInstance.setValue(date);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance.getName(), instance);
        assertNull(ContextInstanceCache.getContextParameter(contextName, "BusinessDate"));
    }

    @Test
    public void getContextParameter_should_return_null_with_context_parameters_null_value() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("BusinessDate");
        contextParameterInstance.setValue(null);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance.getName(), instance);
        assertNull(ContextInstanceCache.getContextParameter(contextName, "BusinessDate"));
    }

    @Test
    public void existsInCache() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextInstanceCache.instance().put(instance.getName(), instance);

        assertTrue(ContextInstanceCache.existsInCache(contextName));
        assertFalse(ContextInstanceCache.existsInCache(null));
        assertFalse(ContextInstanceCache.existsInCache(contextName + RandomStringUtils.randomAlphabetic(5)));

        ContextInstanceCache.instance().remove(null);
        assertTrue(ContextInstanceCache.existsInCache(contextName));

        ContextInstanceCache.instance().remove(contextName);
        assertFalse(ContextInstanceCache.existsInCache(contextName));
    }

    @Test
    public void doesNotExistInCache() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextInstanceCache.instance().put(instance.getName(), instance);

        assertTrue(ContextInstanceCache.doesNotExistInCache(null));
        assertTrue(ContextInstanceCache.doesNotExistInCache(contextName + RandomStringUtils.randomAlphabetic(5)));
        assertFalse(ContextInstanceCache.doesNotExistInCache(contextName));
    }

    @Test
    public void getByContextName_and_remove() {
        String contextName1 = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance1 = new ContextInstanceImpl();
        instance1.setName(contextName1);
        ContextInstanceCache.instance().put(instance1.getName(), instance1);

        String contextName2 = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance2 = new ContextInstanceImpl();
        instance2.setName(contextName2);
        ContextInstanceCache.instance().put(instance2.getName(), instance2);

        assertNull(ContextInstanceCache.instance().getByContextName(null));
        assertEquals(instance1, ContextInstanceCache.instance().getByContextName(contextName1));
        assertEquals(instance2, ContextInstanceCache.instance().getByContextName(contextName2));

        ContextInstanceCache.instance().remove(null);
        assertEquals(instance1, ContextInstanceCache.instance().getByContextName(contextName1));
        assertEquals(instance2, ContextInstanceCache.instance().getByContextName(contextName2));

        ContextInstanceCache.instance().remove(contextName1);
        assertNull(ContextInstanceCache.instance().getByContextName(contextName1));
        assertEquals(instance2, ContextInstanceCache.instance().getByContextName(contextName2));

        ContextInstanceCache.instance().remove(contextName2);
        assertNull(ContextInstanceCache.instance().getByContextName(contextName1));
        assertNull(ContextInstanceCache.instance().getByContextName(contextName2));
    }

    @Test
    public void put() {
        String contextName1 = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance1 = new ContextInstanceImpl();
        instance1.setName(contextName1);

        ContextInstanceCache.instance().put(contextName1, null);
        assertNull(ContextInstanceCache.instance().getByContextName(contextName1));

        ContextInstanceCache.instance().put(null, instance1);
        assertNull(ContextInstanceCache.instance().getByContextName(contextName1));

        ContextInstanceCache.instance().put(instance1.getName(), instance1);
        assertEquals(instance1, ContextInstanceCache.instance().getByContextName(contextName1));
    }

}