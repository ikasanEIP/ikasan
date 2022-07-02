package org.ikasan.spec.scheduled.profile.model;

import java.util.List;

public interface ContextProfile {

    String getDefaultContext();

    void setDefaultContext(String context);

    List<String> getSubContexts();

    void setSubContexts(List<String> subContexts);
}
