package org.ikasan.dashboard.ui.administration.filter;

import com.google.common.collect.Lists;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.security.model.UserLite;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserLightFilterTest {

    private UserLiteFilter userLiteFilter;

    @Before
    public void setup() {

        List<UserLite> userLites = new ArrayList<>();

        for(int i=0; i<100; i++) {
            UserLite userLite = new UserLite();
            userLite.setUsername("username"+i);
            userLite.setDepartment("department"+i);
            userLite.setFirstName("name"+i);
            userLite.setSurname("lastname"+i);
            userLite.setEmail("email"+i);
            userLite.setPreviousAccessTimestamp(i);

            userLites.add(userLite);
        }

        userLiteFilter = new UserLiteFilter();
        userLiteFilter.setItems(userLites);
    }

    @Test
    public void test_success_no_filter() {
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_username() {
        this.userLiteFilter.setUsernameFilter("username");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setUsernameFilter("user");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setUsernameFilter("ame");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setUsernameFilter("username9");
        Assert.assertEquals(11, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setUsernameFilter("bad-name");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_name() {
        this.userLiteFilter.setNameFilter("name");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setNameFilter("na");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setNameFilter("ame");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setNameFilter("name9");
        Assert.assertEquals(11, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setNameFilter("bad-name");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_email() {
        this.userLiteFilter.setEmailFilter("email");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setEmailFilter("em");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setEmailFilter("mail");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setEmailFilter("email9");
        Assert.assertEquals(11, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setEmailFilter("bad-name");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_lastname() {
        this.userLiteFilter.setLastNameFilter("lastname");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setLastNameFilter("lastna");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setLastNameFilter("stname");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setLastNameFilter("lastname9");
        Assert.assertEquals(11, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setLastNameFilter("bad-name");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_description_filter_department() {
        this.userLiteFilter.setDepartmentFilter("department");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setDepartmentFilter("depart");
        Assert.assertEquals(100, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setDepartmentFilter("rtment");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setDepartmentFilter("department9");
        Assert.assertEquals(11, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userLiteFilter.setDepartmentFilter("bad-name");
        Assert.assertEquals(0, userLiteFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_sort_username() {
        QuerySortOrder querySortOrder = new QuerySortOrder("username", SortDirection.ASCENDING);
        Comparator<UserLite> comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<UserLite> users = this.userLiteFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("username", SortDirection.DESCENDING);
        comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_firstname() {
        QuerySortOrder querySortOrder = new QuerySortOrder("firstname", SortDirection.ASCENDING);
        Comparator<UserLite> comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<UserLite> users = this.userLiteFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("firstname", SortDirection.DESCENDING);
        comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_surname() {
        QuerySortOrder querySortOrder = new QuerySortOrder("surname", SortDirection.ASCENDING);
        Comparator<UserLite> comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<UserLite> users = this.userLiteFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("surname", SortDirection.DESCENDING);
        comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_email() {
        QuerySortOrder querySortOrder = new QuerySortOrder("email", SortDirection.ASCENDING);
        Comparator<UserLite> comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<UserLite> users = this.userLiteFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("email", SortDirection.DESCENDING);
        comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_department() {
        QuerySortOrder querySortOrder = new QuerySortOrder("department", SortDirection.ASCENDING);
        Comparator<UserLite> comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<UserLite> users = this.userLiteFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("department", SortDirection.DESCENDING);
        comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_lastaccess() {
        QuerySortOrder querySortOrder = new QuerySortOrder("lastaccess", SortDirection.ASCENDING);
        Comparator<UserLite> comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<UserLite> users = this.userLiteFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("lastaccess", SortDirection.DESCENDING);
        comparator = this.userLiteFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }
}
