package org.ikasan.spec.dashboard;

public interface DashboardRestService<T>
{

    String MODULE_NAME_PROPERTY="module.name";
    String DASHBOARD_BASE_URL_PROPERTY="ikasan.dashboard.extract.base.url";
    String DASHBOARD_USERNAME_PROPERTY="ikasan.dashboard.extract.username";
    String DASHBOARD_PASSWORD_PROPERTY="ikasan.dashboard.extract.password";
    String DASHBOARD_EXTRACT_ENABLED_PROPERTY ="ikasan.dashboard.extract.enabled";

    boolean publish(T t);


}
