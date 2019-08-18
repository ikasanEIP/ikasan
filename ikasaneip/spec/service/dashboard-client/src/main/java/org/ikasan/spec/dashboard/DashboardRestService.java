package org.ikasan.spec.dashboard;

public interface DashboardRestService<T>
{

    String MODULE_NAME_PROPERTY="module.name";
    String DASHBOARD_BASE_URL_PROPERTY="ikasan.dashboard.base.url";
    String DASHBOARD_USERNAME_PROPERTY="ikasan.dashboard.username";
    String DASHBOARD_PASSWORD_PROPERTY="ikasan.dashboard.password";
    String HARVESTING_ENABLED_PROPERTY="ikasan.harvesting.enabled";

    boolean publish(T t);


}
