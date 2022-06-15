package org.ikasan.ootb.scheduler.agent.rest.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.ikasan.job.orchestration.model.context.ContextInstanceImpl;
import org.ikasan.job.orchestration.model.context.ContextParameterInstanceImpl;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.junit.Test;

public class ContextInstanceCacheUtilTest {

    @Test
    public void should_retrieve_context_parameter() {
        // null in the cache
        String param = ContextInstanceCacheUtil.getParameterValue("unknown", "Original Value");
        assertNull(param);

        // null context params in the cache
        ContextInstanceImpl instance = new ContextInstanceImpl();
        String name = RandomStringUtils.randomAlphabetic(12);
        instance.setName(name);
        ContextInstanceCache.instance().put(instance);
        param = ContextInstanceCacheUtil.getParameterValue(name, "Original Value");
        assertNull(param);

        // empty context params in the cache
        List<ContextParameterInstance> params = new ArrayList<>();
        instance.setContextParameters(params);
        ContextInstanceCache.instance().put(instance);

        param = ContextInstanceCacheUtil.getParameterValue(name, "Original Value");
        assertNull(param);

        params.add(createParam("original value", "New Value"));
        params.add(createParam("Original Value 1", "New Value 1"));
        params.add(createParam("Original Value 2", 1));

        instance.setContextParameters(params);
        ContextInstanceCache.instance().put(instance);

        param = ContextInstanceCacheUtil.getParameterValue(name, "Original Value");
        assertEquals("New Value", param);

        param = ContextInstanceCacheUtil.getParameterValue(name, "original value 1");
        assertEquals("New Value 1", param);

        param = ContextInstanceCacheUtil.getParameterValue(name, "Original Value 2");
        assertEquals("1", param);
    }

    private ContextParameterInstance createParam(String name, Object value) {
        ContextParameterInstanceImpl param = new ContextParameterInstanceImpl();
        param.setName(name);
        param.setType("java.lang.String");
        param.setValue(value);
        return param;
    }
}