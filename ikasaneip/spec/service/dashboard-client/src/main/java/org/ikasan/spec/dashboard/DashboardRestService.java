package org.ikasan.spec.dashboard;

import org.ikasan.harvest.HarvestEvent;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;

import java.util.List;

public interface DashboardRestService
{

    String MODULE_NAME_PROPERTY="module.name";
    String DASHBOARD_BASE_URL_PROPERTY="ikasan.dashboard.base.url";
    String DASHBOARD_USERNAME_PROPERTY="ikasan.dashboard.username";
    String DASHBOARD_PASSWORD_PROPERTY="ikasan.dashboard.password";
    String HARVESTING_ENABLED_PROPERTY="ikasan.harvesting.enabled";

    boolean publish(List<HarvestEvent> events);

    boolean publish(Module<Flow> module);
}
