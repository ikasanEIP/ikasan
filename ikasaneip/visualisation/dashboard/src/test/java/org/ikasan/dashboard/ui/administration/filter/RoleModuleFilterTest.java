package org.ikasan.dashboard.ui.administration.filter;

import com.google.common.collect.Lists;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.security.model.RoleModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoleModuleFilterTest {

    private RoleModuleFilter roleModuleFilter;

    @Before
    public void setup() {

        List<RoleModule> roleModules = new ArrayList<>();

        for(int i=0; i<100; i++) {
            RoleModule roleModule = new RoleModule();
            roleModule.setModuleName("name"+i);

            roleModules.add(roleModule);
        }

        roleModuleFilter = new RoleModuleFilter();
        roleModuleFilter.setItems(roleModules);
    }

    @Test
    public void test_success_no_filter() {
        Assert.assertEquals(100, roleModuleFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_name() {
        this.roleModuleFilter.setModuleNameFilter("name");
        Assert.assertEquals(100, roleModuleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleModuleFilter.setModuleNameFilter("na");
        Assert.assertEquals(100, roleModuleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleModuleFilter.setModuleNameFilter("ame");
        Assert.assertEquals(100, roleModuleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleModuleFilter.setModuleNameFilter("ame9");
        Assert.assertEquals(11, roleModuleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleModuleFilter.setModuleNameFilter("bad-name");
        Assert.assertEquals(0, roleModuleFilter.getFilterStream().collect(Collectors.toList()).size());
    }


    @Test
    public void test_success_sort_name() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<RoleModule> comparator = this.roleModuleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<RoleModule> roleModules = this.roleModuleFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(roleModules.get(0), roleModules.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.roleModuleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(roleModules.get(1), roleModules.get(0)));
    }
}
