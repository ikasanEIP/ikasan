package org.ikasan.dashboard;

import org.ikasan.dashboard.notification.BusinessStreamNotificationJobTest;
import org.ikasan.dashboard.notification.service.BusinessStreamNotificationServiceTest;
import org.ikasan.dashboard.security.schedule.LdapDirectorySynchronisationJobTest;
import org.ikasan.dashboard.ui.administration.filter.*;
import org.ikasan.dashboard.ui.administration.view.*;
import org.ikasan.dashboard.ui.general.component.EventLifeIdDeepLinkViewTest;
import org.ikasan.dashboard.ui.general.component.FilteringGridTest;
import org.ikasan.dashboard.ui.general.component.HospitalViewTest;
import org.ikasan.dashboard.ui.general.component.SearchResultTest;
import org.ikasan.dashboard.ui.layout.IkasanAppLayoutTest;
import org.ikasan.dashboard.ui.org.ikasan.dashboard.broadcast.FlowStateBroadcasterTest;
import org.ikasan.dashboard.ui.search.component.SearchFormTest;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGridTest;
import org.ikasan.dashboard.ui.search.view.SearchViewTest;
import org.ikasan.dashboard.ui.visualisation.adapter.service.BusinessStreamVisjsAdapterTest;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapterTest;
import org.ikasan.dashboard.ui.visualisation.component.ComponentConfigurationDialogTest;
import org.ikasan.dashboard.ui.visualisation.component.ModuleFilteringGridTest;
import org.ikasan.dashboard.ui.visualisation.dao.ModuleMetaDataDaoImplTest;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanFlowLayoutManagerTest;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanModuleLayoutManagerTest;
import org.ikasan.dashboard.ui.visualisation.view.BusinessStreamViewTest;
import org.ikasan.dashboard.ui.visualisation.view.ModuleVisualisationViewTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
    FilteringGridTest.class,
    GroupFilterTest.class,
    ModuleFilterTest.class,
    PolicyFilterTest.class,
    RoleFilterTest.class,
    RoleModuleFilterTest.class,
    UserFilterTest.class,
    UserLightFilterTest.class,
    BusinessStreamNotificationServiceTest.class,
    BusinessStreamNotificationJobTest.class,
    GroupManagementViewTest.class,
    PolicyManagementViewTest.class,
    RoleManagementViewTest.class,
    UserDirectoriesViewTest.class,
    UserManagementViewTest.class,
    FlowStateBroadcasterTest.class,
    SearchViewTest.class,
    SolrSearchFilteringGridTest.class,
    SearchFormTest.class,
    BusinessStreamVisjsAdapterTest.class,
    ModuleVisjsAdapterTest.class,
    ModuleMetaDataDaoImplTest.class,
    IkasanFlowLayoutManagerTest.class,
    IkasanModuleLayoutManagerTest.class,
    BusinessStreamViewTest.class,
    HospitalViewTest.class,
    SearchResultTest.class,
    EventLifeIdDeepLinkViewTest.class,
    ComponentConfigurationDialogTest.class,
    ModuleFilteringGridTest.class,
    LdapDirectorySynchronisationJobTest.class,
    ModuleVisualisationViewTest.class,
    SystemEventSearchViewTest.class,
    AdministrationSearchViewTest.class,
    IkasanAppLayoutTest.class
})
public class TestSuite {
}