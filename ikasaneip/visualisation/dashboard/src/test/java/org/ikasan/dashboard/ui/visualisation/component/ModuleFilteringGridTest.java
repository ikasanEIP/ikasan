package org.ikasan.dashboard.ui.visualisation.component;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.UI;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.util.SearchConstants;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.component.filter.ModuleSearchFilter;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.RoleModule;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.metadata.ModuleMetadataSearchResults;
import org.ikasan.topology.metadata.model.ModuleMetaDataImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;

public class ModuleFilteringGridTest extends UITest {
    @MockBean
    private ModuleMetaDataService moduleMetaDataService;

    @MockBean
    private ModuleMetadataSearchResults moduleMetadataSearchResults;

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
    }

    @Test
    public void test_search_non_admin_user_search_no_associated_modules_should_have_empty_module_grid()
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

        Mockito.when(this.moduleMetaDataService.find(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals(SearchConstants.NONSENSE_STRING))
            , Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(this.moduleMetadataSearchResults);

        Mockito.when(this.moduleMetadataSearchResults.getResultList())
            .thenReturn(this.getModuleMetaData(0));
        Mockito.when(this.moduleMetadataSearchResults.getTotalNumberOfResults())
            .thenReturn(0L);
        Mockito.when(this.moduleMetadataSearchResults.getQueryResponseTime())
            .thenReturn(100L);

        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);
        Assertions.assertNotNull(graphView);

        ModuleFilteringGrid moduleFilteringGrid = (ModuleFilteringGrid)ReflectionTestUtils.getField(graphView, "modulesGrid");
        Assertions.assertNotNull(moduleFilteringGrid);

        Assert.assertEquals(0, GridKt._size(moduleFilteringGrid));
    }

    @Test
    public void test_search_non_admin_user_search_with_associated_modules_should_have_module_filtered_results()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);
        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator(), Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator(), Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator(), Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("moduleName0");

        Mockito.when(this.moduleMetaDataService.find(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("moduleName0"))
            , Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(this.moduleMetadataSearchResults);

        Mockito.when(this.moduleMetadataSearchResults.getResultList())
            .thenReturn(this.getModuleMetaData(1));
        Mockito.when(this.moduleMetadataSearchResults.getTotalNumberOfResults())
            .thenReturn(1L);
        Mockito.when(this.moduleMetadataSearchResults.getQueryResponseTime())
            .thenReturn(100L);

        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);
        Assertions.assertNotNull(graphView);

        ModuleFilteringGrid moduleFilteringGrid = (ModuleFilteringGrid)ReflectionTestUtils.getField(graphView, "modulesGrid");
        Assertions.assertNotNull(moduleFilteringGrid);

        Assert.assertEquals(1, GridKt._size(moduleFilteringGrid));

        ModuleMetaData moduleMetaData = GridKt._get(moduleFilteringGrid, 0);
        Assert.assertEquals("moduleName0", moduleMetaData.getName());
        Assert.assertEquals("url0", moduleMetaData.getUrl());
        Assert.assertEquals("description0", moduleMetaData.getDescription());
        Assert.assertEquals("version0", moduleMetaData.getVersion());
    }

    @Test
    public void test_search_admin_user_search()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.moduleMetaDataService.find(argThat(strings -> strings.size() == 0)
            , Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(this.moduleMetadataSearchResults);

        Mockito.when(this.moduleMetadataSearchResults.getResultList())
            .thenReturn(this.getModuleMetaData(25));
        Mockito.when(this.moduleMetadataSearchResults.getTotalNumberOfResults())
            .thenReturn(25L);
        Mockito.when(this.moduleMetadataSearchResults.getQueryResponseTime())
            .thenReturn(100L);

        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);
        Assertions.assertNotNull(graphView);

        ModuleFilteringGrid moduleFilteringGrid = (ModuleFilteringGrid)ReflectionTestUtils.getField(graphView, "modulesGrid");
        Assertions.assertNotNull(moduleFilteringGrid);

        Assert.assertEquals(25, GridKt._size(moduleFilteringGrid));

        List<ModuleMetaData> moduleMetaData = GridKt._findAll(moduleFilteringGrid);
        Assert.assertEquals("moduleName0", moduleMetaData.get(0).getName());
        Assert.assertEquals("url0", moduleMetaData.get(0).getUrl());
        Assert.assertEquals("description0", moduleMetaData.get(0).getDescription());
        Assert.assertEquals("version0", moduleMetaData.get(0).getVersion());
    }

    @Test
    public void test_search_admin_user_search_and_filter()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.moduleMetaDataService.find(argThat(strings -> strings != null && strings.size() == 0)
            , Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(this.moduleMetadataSearchResults);

        Mockito.when(this.moduleMetadataSearchResults.getResultList())
            .thenReturn(this.getModuleMetaData(25));
        Mockito.when(this.moduleMetadataSearchResults.getTotalNumberOfResults())
            .thenReturn(25L);
        Mockito.when(this.moduleMetadataSearchResults.getQueryResponseTime())
            .thenReturn(100L);

        ModuleMetadataSearchResults results = mock(ModuleMetadataSearchResults.class);

        Mockito.when(this.moduleMetaDataService.find(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("*module*"))
            , Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(results);

        Mockito.when(results.getResultList())
            .thenReturn(this.getModuleMetaData(1));
        Mockito.when(results.getTotalNumberOfResults())
            .thenReturn(1L);
        Mockito.when(results.getQueryResponseTime())
            .thenReturn(100L);

        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);
        Assertions.assertNotNull(graphView);

        ModuleFilteringGrid moduleFilteringGrid = (ModuleFilteringGrid)ReflectionTestUtils.getField(graphView, "modulesGrid");
        Assertions.assertNotNull(moduleFilteringGrid);

        Assert.assertEquals(25, GridKt._size(moduleFilteringGrid));

        ModuleSearchFilter searchFilter = (ModuleSearchFilter)ReflectionTestUtils.getField(moduleFilteringGrid, "searchFilter");
        searchFilter.setModuleNameFilter("module");

        Assert.assertEquals(1, GridKt._size(moduleFilteringGrid));

        ModuleMetaData moduleMetaData = GridKt._get(moduleFilteringGrid, 0);
        Assert.assertEquals("moduleName0", moduleMetaData.getName());
        Assert.assertEquals("url0", moduleMetaData.getUrl());
        Assert.assertEquals("description0", moduleMetaData.getDescription());
        Assert.assertEquals("version0", moduleMetaData.getVersion());
    }

    @Test
    public void test_search_non_admin_user_search_and_filter()
    {
        Mockito.when(super.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(false);

        Mockito.when(super.ikasanAuthentication.getPrincipal())
            .thenReturn(super.user);
        Mockito.when(super.user.getPrincipals())
            .thenReturn(this.principals);
        Mockito.doCallRealMethod().when(this.principals).forEach((any(Consumer.class)));
        Mockito.when(this.principals.iterator()).thenReturn(Set.of(principal).iterator(), Set.of(principal).iterator(), Set.of(principal).iterator(), Set.of(principal).iterator());
        Mockito.when(principal.getRoles()).thenReturn(this.roles);
        Mockito.doCallRealMethod().when(this.roles).forEach((any(Consumer.class)));
        Mockito.when(this.roles.iterator()).thenReturn(Set.of(role).iterator(), Set.of(role).iterator(), Set.of(role).iterator(), Set.of(role).iterator());
        Mockito.when(role.getRoleModules()).thenReturn(this.roleModules);
        Mockito.doCallRealMethod().when(this.roleModules).forEach((any(Consumer.class)));
        Mockito.when(this.roleModules.iterator()).thenReturn(Set.of(roleModule).iterator(), Set.of(roleModule).iterator(), Set.of(roleModule).iterator(), Set.of(roleModule).iterator());
        Mockito.when(this.roleModule.getModuleName()).thenReturn("someModule", "moduleName0");

        Mockito.when(this.moduleMetaDataService.find(argThat(strings -> strings != null && strings.size() == 1 && strings.stream().findFirst().get().equals("someModule"))
            , Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(this.moduleMetadataSearchResults);

        Mockito.when(this.moduleMetadataSearchResults.getResultList())
            .thenReturn(this.getModuleMetaData(25));
        Mockito.when(this.moduleMetadataSearchResults.getTotalNumberOfResults())
            .thenReturn(25L);
        Mockito.when(this.moduleMetadataSearchResults.getQueryResponseTime())
            .thenReturn(100L);

        ModuleMetadataSearchResults results = mock(ModuleMetadataSearchResults.class);

        Mockito.when(this.moduleMetaDataService.find(argThat(strings -> strings.size() == 1 && strings.stream().findFirst().get().equals("moduleName0"))
            , Mockito.anyInt(), Mockito.anyInt()))
            .thenReturn(results);

        Mockito.when(results.getResultList())
            .thenReturn(this.getModuleMetaData(1));
        Mockito.when(results.getTotalNumberOfResults())
            .thenReturn(1L);
        Mockito.when(results.getQueryResponseTime())
            .thenReturn(100L);

        UI.getCurrent().navigate("visualisation");

        GraphView graphView = _get(GraphView.class);
        Assertions.assertNotNull(graphView);

        ModuleFilteringGrid moduleFilteringGrid = (ModuleFilteringGrid)ReflectionTestUtils.getField(graphView, "modulesGrid");
        Assertions.assertNotNull(moduleFilteringGrid);

        Assert.assertEquals(25, GridKt._size(moduleFilteringGrid));

        ModuleSearchFilter searchFilter = (ModuleSearchFilter)ReflectionTestUtils.getField(moduleFilteringGrid, "searchFilter");
        searchFilter.setModuleNameFilter("module");

        Assert.assertEquals(1, GridKt._size(moduleFilteringGrid));

        ModuleMetaData moduleMetaData = GridKt._get(moduleFilteringGrid, 0);
        Assert.assertEquals("moduleName0", moduleMetaData.getName());
        Assert.assertEquals("url0", moduleMetaData.getUrl());
        Assert.assertEquals("description0", moduleMetaData.getDescription());
        Assert.assertEquals("version0", moduleMetaData.getVersion());
    }

    private List<ModuleMetaData> getModuleMetaData(int num) {
        List<ModuleMetaData> moduleMetaDataList = new ArrayList<>();

        IntStream.range(0, num).forEach(i -> {
            ModuleMetaDataImpl moduleMetaData = new ModuleMetaDataImpl();
            moduleMetaData.setName("moduleName"+i);
            moduleMetaData.setUrl("url"+i);
            moduleMetaData.setDescription("description"+i);
            moduleMetaData.setVersion("version"+i);

            moduleMetaDataList.add(moduleMetaData);
        });

        return moduleMetaDataList;
    }
}
