package org.ikasan.dashboard.ui.search.component;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.util.SearchConstants;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.RoleModule;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.mockito.ArgumentMatchers.*;

public class SolrSearchFilteringGridTest extends UITest {
    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private Set<IkasanPrincipal> principals;

    @MockBean
    private IkasanPrincipal principal;

    @MockBean
    private Set<Role> roles;

    @MockBean
    private Role role;

    @MockBean
    private Set<RoleModule> roleModules;

    @MockBean
    private RoleModule roleModule;

    public void setup_expectations() {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(50));

        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));
    }

    @Test
    public void test_filtered_data_provider_not_null_after_search() throws IOException
    {
        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertNotNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));
    }

    @Test
    public void test_filtered_data_provider_null_before_search() throws IOException
    {
        UI.getCurrent().navigate("");

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        Assert.assertNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));

    }

    @Test
    public void test_filtered_data_provider_null_before_search_add_filter() throws IOException
    {
        UI.getCurrent().navigate("");

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event");

        Assert.assertNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));
    }

    @Test
    public void test_search_and_filter_user_all()
    {
        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertNotNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));

        Assert.assertEquals(50, solrSearchFilteringGrid.getResultSize());

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_non_admin_user_search_no_associated_modules()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(new HashSet<RoleModule>().iterator(), new HashSet<RoleModule>().iterator());
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals(SearchConstants.NONSENSE_STRING)),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_non_admin_user_search_with_module_name_filter_no_associated_modules()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(new HashSet<RoleModule>().iterator(), new HashSet<RoleModule>().iterator());
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals(SearchConstants.NONSENSE_STRING)),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField moduleNameeFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        moduleNameeFilter.setValue("test");

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_non_admin_user_search_with_associated_module_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_admin_user_search_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);
        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_non_admin_user_search_with_associated_module_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_admin_user_search_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_non_admin_user_search_with_associated_module_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_admin_user_search_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);
        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_non_admin_user_search_with_associated_module_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_admin_user_search_wiretap()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("wiretap")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_non_admin_user_search_with_associated_module_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_admin_user_search_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_non_admin_user_search_with_associated_module_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_admin_user_search_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_non_admin_user_search_with_associated_module_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_admin_user_search_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_non_admin_user_search_with_associated_module_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_admin_user_search_replay_event()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_non_admin_user_search_with_associated_module_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_admin_user_search_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_non_admin_user_search_with_associated_module_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_admin_user_search_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_non_admin_user_search_with_associated_module_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_admin_user_search_with_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_non_admin_user_search_with_associated_module_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_admin_user_search_exclusion()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("exclusion")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_non_admin_user_search_with_associated_module_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_admin_user_search_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_non_admin_user_search_with_associated_module_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_admin_user_search_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_non_admin_user_search_with_associated_module_replay_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_admin_user_search_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_non_admin_user_search_with_associated_module_replay_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_admin_user_search_error()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("error")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_non_admin_user_search_with_associated_module_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_event_id_filter_admin_user_search_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), eq("*event1*"), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_non_admin_user_search_with_associated_module_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_module_name_filter_admin_user_search_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(),
            Mockito.anyInt(), argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("moduleName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_non_admin_user_search_with_associated_module_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_flow_name_filter_admin_user_search_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("flowName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_non_admin_user_search_with_associated_module_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_component_name_filter_admin_user_search_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*test*")), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("componentName"));
        eventFilter.setValue("test");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_no_filter_non_admin_user_search_with_associated_module_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("testModuleName");
        Mockito.when(this.solrSearchService.search(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("testModuleName")),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_no_filter_admin_user_search_all_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);
        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 4 && strings.contains("error") && strings.contains("exclusion") && strings.contains("wiretap") && strings.contains("replay")), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_no_filter_admin_user_search_no_event_types()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);
        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            argThat(strings -> strings.size() == 1 && strings.contains(SearchConstants.NONSENSE_STRING)), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(0));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(0, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_with_sort_order()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);
        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            Mockito.anyList(), Mockito.anyBoolean(),
            Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(10));
        Mockito.when(this.solrSearchService.search(Mockito.isNull(),
            Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
            Mockito.anyList(), Mockito.anyBoolean(),
            eq("moduleName"), eq("ASCENDING")))
            .thenReturn(this.getSolrResults(10));

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        GridKt.sort(solrSearchFilteringGrid, new QuerySortOrder("moduleName", SortDirection.ASCENDING));

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(10, solrSearchFilteringGrid.getResultSize());
    }

}
