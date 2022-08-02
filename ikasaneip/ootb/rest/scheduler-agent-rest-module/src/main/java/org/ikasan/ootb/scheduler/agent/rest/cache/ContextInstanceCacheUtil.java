package org.ikasan.ootb.scheduler.agent.rest.cache;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ContextInstanceCacheUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ContextInstanceCacheUtil.class);

    public static String getContextParameter(String contextName, String contextParameterName) {
        if (contextName != null && contextParameterName != null) {
            ContextInstance instance = ContextInstanceCache.instance().getByContextName(contextName);
            if (instance != null) {
                List<ContextParameterInstance> contextParameters = instance.getContextParameters();
                if (contextParameters != null) {
                    for (ContextParameterInstance contextParameter : contextParameters) {
                        if (contextParameter.getName() != null
                            && contextParameter.getValue() != null
                            && contextParameterName.equals(contextParameter.getName())) {
                            return contextParameter.getValue().toString();
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean existsInCache(String contextName) {
        return ContextInstanceCache.instance().getByContextName(contextName) != null;
    }

    public static boolean doesNotExistInCache(String contextName) {
        return !existsInCache(contextName);
    }
}
