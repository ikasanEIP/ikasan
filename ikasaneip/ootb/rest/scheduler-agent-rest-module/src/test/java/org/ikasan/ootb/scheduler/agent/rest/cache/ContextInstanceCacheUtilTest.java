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

public class ContextInstanceCacheUtilTest {

    @Test
    public void should_return_null_values() {
        assertNull(ContextInstanceCacheUtil.getContextParameter(null, "contextParameterName"));
        assertNull(ContextInstanceCacheUtil.getContextParameter("contextName", null));
    }


    @Test
    public void should_return_null_null_context_instance() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        assertNull(ContextInstanceCacheUtil.getContextParameter(contextName, "contextParameterName"));
    }

    @Test
    public void should_return_null_null_context_parameters() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);

        ContextInstanceCache.instance().put(instance);
        assertNull(ContextInstanceCacheUtil.getContextParameter(contextName, "contextParameterName"));
    }

    @Test
    public void should_return_value() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("BusinessDate");
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        contextParameterInstance.setValue(date);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance);
        assertEquals(date, (ContextInstanceCacheUtil.getContextParameter(contextName, contextParameterInstance.getName())));
    }

    @Test
    public void should_return_null_with_context_parameters_null_name() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName(null);
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        contextParameterInstance.setValue(date);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance);
        assertNull(ContextInstanceCacheUtil.getContextParameter(contextName, "BusinessDate"));
    }

    @Test
    public void should_return_null_with_context_parameters_null_value() {
        String contextName = RandomStringUtils.randomAlphanumeric(12);
        ContextInstance instance = new ContextInstanceImpl();
        instance.setName(contextName);
        ContextParameterInstance contextParameterInstance = new ContextParameterInstanceImpl();
        contextParameterInstance.setName("BusinessDate");
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        contextParameterInstance.setValue(null);
        List<ContextParameterInstance> params = List.of(contextParameterInstance);
        instance.setContextParameters(params);

        ContextInstanceCache.instance().put(instance);
        assertNull(ContextInstanceCacheUtil.getContextParameter(contextName, "BusinessDate"));
    }

}