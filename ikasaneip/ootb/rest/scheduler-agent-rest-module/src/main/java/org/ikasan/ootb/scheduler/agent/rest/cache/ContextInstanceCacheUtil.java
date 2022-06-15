package org.ikasan.ootb.scheduler.agent.rest.cache;

import org.ikasan.spec.scheduled.instance.model.ContextInstance;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

public class ContextInstanceCacheUtil {

    public static String getParameterValue(String contextName, String parameterValue) {
        ContextInstance instance = ContextInstanceCache.instance().getByContextName(contextName);
        if (instance != null && instance.getContextParameters() != null) {
            for (ContextParameterInstance contextParameterInstance : instance.getContextParameters()) {
                if (parameterValue.equalsIgnoreCase(contextParameterInstance.getName())) {
                    return contextParameterInstance.getValue().toString();
                }
            }
        }
        return null;
    }
}
