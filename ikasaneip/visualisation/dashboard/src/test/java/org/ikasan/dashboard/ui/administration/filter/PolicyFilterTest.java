package org.ikasan.dashboard.ui.administration.filter;

import com.google.common.collect.Lists;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.security.model.Policy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PolicyFilterTest {

    private PolicyFilter policyFilter;

    @Before
    public void setup() {

        List<Policy> policies = new ArrayList<>();

        for(int i=0; i<100; i++) {
            Policy policy = new Policy();
            policy.setName("name"+i);
            policy.setDescription("description"+i);

            policies.add(policy);
        }

        policyFilter = new PolicyFilter();
        policyFilter.setItems(policies);
    }

    @Test
    public void test_success_no_filter() {
        Assert.assertEquals(100, policyFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_name() {
        this.policyFilter.setNameFilter("name");
        Assert.assertEquals(100, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setNameFilter("na");
        Assert.assertEquals(100, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setNameFilter("ame");
        Assert.assertEquals(100, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setNameFilter("ame9");
        Assert.assertEquals(11, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setNameFilter("bad-name");
        Assert.assertEquals(0, policyFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_description_filter_type() {
        this.policyFilter.setDescriptionFilter("description");
        Assert.assertEquals(100, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setDescriptionFilter("descr");
        Assert.assertEquals(100, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setDescriptionFilter("ription");
        Assert.assertEquals(100, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setDescriptionFilter("tion9");
        Assert.assertEquals(11, policyFilter.getFilterStream().collect(Collectors.toList()).size());
        this.policyFilter.setDescriptionFilter("bad-name");
        Assert.assertEquals(0, policyFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_sort_name() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<Policy> comparator = this.policyFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<Policy> policies = this.policyFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(policies.get(0), policies.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.policyFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(policies.get(1), policies.get(0)));
    }

    @Test
    public void test_success_sort_description() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<Policy> comparator = this.policyFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<Policy> policies = this.policyFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(policies.get(0), policies.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.policyFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(policies.get(1), policies.get(0)));
    }
}
