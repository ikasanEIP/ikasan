package org.ikasan.dashboard.ui.administration.filter;

import com.google.common.collect.Lists;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.security.model.IkasanPrincipalLite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GroupFilterTest {

    private GroupFilter groupFilter;

    @Before
    public void setup() {

        List<IkasanPrincipalLite> groups = new ArrayList<>();

        for(int i=0; i<100; i++) {
            IkasanPrincipalLite ikasanPrincipalLite = new IkasanPrincipalLite();
            ikasanPrincipalLite.setName("name"+i);
            ikasanPrincipalLite.setType("type"+i);
            ikasanPrincipalLite.setDescription("description"+i);

            groups.add(ikasanPrincipalLite);
        }

        groupFilter = new GroupFilter();
        groupFilter.setItems(groups);
    }

    @Test
    public void test_success_no_filter() {
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_name() {
        this.groupFilter.setNameFilter("name");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setNameFilter("na");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setNameFilter("ame");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setNameFilter("ame9");
        Assert.assertEquals(11, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setNameFilter("bad-name");
        Assert.assertEquals(0, groupFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_type() {
        this.groupFilter.setTypeFilter("type");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setTypeFilter("ty");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setTypeFilter("ype");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setTypeFilter("ype9");
        Assert.assertEquals(11, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setTypeFilter("bad-name");
        Assert.assertEquals(0, groupFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_description_filter_type() {
        this.groupFilter.setDescriptionFilter("description");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setDescriptionFilter("descr");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setDescriptionFilter("ription");
        Assert.assertEquals(100, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setDescriptionFilter("tion9");
        Assert.assertEquals(11, groupFilter.getFilterStream().collect(Collectors.toList()).size());
        this.groupFilter.setDescriptionFilter("bad-name");
        Assert.assertEquals(0, groupFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_sort_name() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<IkasanPrincipalLite> comparator = this.groupFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<IkasanPrincipalLite> ikasanPrincipalLites = this.groupFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(ikasanPrincipalLites.get(0), ikasanPrincipalLites.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.groupFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(ikasanPrincipalLites.get(1), ikasanPrincipalLites.get(0)));
    }

    @Test
    public void test_success_sort_type() {
        QuerySortOrder querySortOrder = new QuerySortOrder("type", SortDirection.ASCENDING);
        Comparator<IkasanPrincipalLite> comparator = this.groupFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<IkasanPrincipalLite> ikasanPrincipalLites = this.groupFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(ikasanPrincipalLites.get(0), ikasanPrincipalLites.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.groupFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(ikasanPrincipalLites.get(1), ikasanPrincipalLites.get(0)));
    }

    @Test
    public void test_success_sort_description() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<IkasanPrincipalLite> comparator = this.groupFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<IkasanPrincipalLite> ikasanPrincipalLites = this.groupFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(ikasanPrincipalLites.get(0), ikasanPrincipalLites.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.groupFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(ikasanPrincipalLites.get(1), ikasanPrincipalLites.get(0)));
    }
}
