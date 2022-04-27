package org.ikasan.ootb.scheduler.agent.module.component.cache;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

import java.util.List;

public class ContextParametersCacheUtil {

    public static Object findContextualPlaceholderParamValue(String contextName, String contextParameterName) {
        List<ContextParameterInstance> contextParameters = ContextParametersCache.instance().getByContextName(contextName);
        for (ContextParameterInstance contextParameterInstance : contextParameters) {
            if (contextParameterInstance.getName().equalsIgnoreCase(contextParameterName)) {
                return contextParameterInstance.getValue();
            }
        }
        return null;
    }

    public static String resolveContextualPlaceholderParam(String contextName, String fullParameterValue) {
        List<ContextParameterInstance> contextParameters = ContextParametersCache.instance().getByContextName(contextName);
        for (ContextParameterInstance contextParameterInstance : contextParameters) {
            if (fullParameterValue.contains(contextParameterInstance.getName())) {
                return fullParameterValue.replace(contextParameterInstance.getName(), contextParameterInstance.getValue().toString());
            }
        }
        return fullParameterValue;
    }


}
