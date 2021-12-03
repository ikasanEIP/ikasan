package org.ikasan.spec.dashboard;

public interface DashboardRestService<T>
{

    public static final String MODULE_NAME_PROPERTY="module.name";
    public static final String DASHBOARD_BASE_URL_PROPERTY="ikasan.dashboard.extract.base.url";
    public static final String DASHBOARD_USERNAME_PROPERTY="ikasan.dashboard.extract.username";
    public static final String DASHBOARD_PASSWORD_PROPERTY="ikasan.dashboard.extract.password";
    public static final String DASHBOARD_EXTRACT_ENABLED_PROPERTY ="ikasan.dashboard.extract.enabled";
    public static final String DASHBOARD_EXTRACT_EXCEPTIONS_PROPERTY ="ikasan.dashboard.extract.exceptions";

    boolean publish(T t);


}
