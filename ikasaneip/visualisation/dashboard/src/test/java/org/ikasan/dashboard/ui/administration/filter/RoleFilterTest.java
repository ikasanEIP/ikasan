package org.ikasan.dashboard.ui.administration.filter;

import com.google.common.collect.Lists;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.security.model.Role;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RoleFilterTest {

    private RoleFilter roleFilter;

    @Before
    public void setup() {

        List<Role> roles = new ArrayList<>();

        for(int i=0; i<100; i++) {
            Role role = new Role();
            role.setName("name"+i);
            role.setDescription("description"+i);

            roles.add(role);
        }

        roleFilter = new RoleFilter();
        roleFilter.setItems(roles);
    }

    @Test
    public void test_success_no_filter() {
        Assert.assertEquals(100, roleFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_name() {
        this.roleFilter.setNameFilter("name");
        Assert.assertEquals(100, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setNameFilter("na");
        Assert.assertEquals(100, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setNameFilter("ame");
        Assert.assertEquals(100, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setNameFilter("ame9");
        Assert.assertEquals(11, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setNameFilter("bad-name");
        Assert.assertEquals(0, roleFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_description_filter_type() {
        this.roleFilter.setDescriptionFilter("description");
        Assert.assertEquals(100, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setDescriptionFilter("descr");
        Assert.assertEquals(100, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setDescriptionFilter("ription");
        Assert.assertEquals(100, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setDescriptionFilter("tion9");
        Assert.assertEquals(11, roleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.roleFilter.setDescriptionFilter("bad-name");
        Assert.assertEquals(0, roleFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_sort_name() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<Role> comparator = this.roleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<Role> roles = this.roleFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(roles.get(0), roles.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.roleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(roles.get(1), roles.get(0)));
    }

    @Test
    public void test_success_sort_description() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<Role> comparator = this.roleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<Role> roles = this.roleFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(roles.get(0), roles.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.roleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(roles.get(1), roles.get(0)));
    }
}
