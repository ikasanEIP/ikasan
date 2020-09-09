package org.ikasan.dashboard.ui.administration.filter;

import com.google.common.collect.Lists;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class UserFilterTest {

    private UserFilter userFilter;

    @Before
    public void setup() {

        List<User> users = new ArrayList<>();

        for(int i=0; i<100; i++) {
            User user = new User();
            user.setUsername("username"+i);
            user.setDepartment("department"+i);
            user.setFirstName("name"+i);
            user.setSurname("lastname"+i);
            user.setEmail("email"+i);
            user.setPreviousAccessTimestamp(i);

            users.add(user);
        }

        userFilter = new UserFilter();
        userFilter.setItems(users);
    }

    @Test
    public void test_success_no_filter() {
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_username() {
        this.userFilter.setUsernameFilter("username");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setUsernameFilter("user");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setUsernameFilter("ame");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setUsernameFilter("username9");
        Assert.assertEquals(11, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setUsernameFilter("bad-name");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_name() {
        this.userFilter.setNameFilter("name");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setNameFilter("na");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setNameFilter("ame");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setNameFilter("name9");
        Assert.assertEquals(11, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setNameFilter("bad-name");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_email() {
        this.userFilter.setEmailFilter("email");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setEmailFilter("em");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setEmailFilter("mail");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setEmailFilter("email9");
        Assert.assertEquals(11, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setEmailFilter("bad-name");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_lastname() {
        this.userFilter.setLastNameFilter("lastname");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setLastNameFilter("lastna");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setLastNameFilter("stname");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setLastNameFilter("lastname9");
        Assert.assertEquals(11, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setLastNameFilter("bad-name");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_description_filter_department() {
        this.userFilter.setDepartmentFilter("department");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setDepartmentFilter("depart");
        Assert.assertEquals(100, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setDepartmentFilter("rtment");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setDepartmentFilter("department9");
        Assert.assertEquals(11, userFilter.getFilterStream().collect(Collectors.toList()).size());
        this.userFilter.setDepartmentFilter("bad-name");
        Assert.assertEquals(0, userFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_sort_username() {
        QuerySortOrder querySortOrder = new QuerySortOrder("username", SortDirection.ASCENDING);
        Comparator<User> comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<User> users = this.userFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("username", SortDirection.DESCENDING);
        comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_firstname() {
        QuerySortOrder querySortOrder = new QuerySortOrder("firstname", SortDirection.ASCENDING);
        Comparator<User> comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<User> users = this.userFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("firstname", SortDirection.DESCENDING);
        comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_surname() {
        QuerySortOrder querySortOrder = new QuerySortOrder("surname", SortDirection.ASCENDING);
        Comparator<User> comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<User> users = this.userFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("surname", SortDirection.DESCENDING);
        comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_email() {
        QuerySortOrder querySortOrder = new QuerySortOrder("email", SortDirection.ASCENDING);
        Comparator<User> comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<User> users = this.userFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("email", SortDirection.DESCENDING);
        comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_department() {
        QuerySortOrder querySortOrder = new QuerySortOrder("department", SortDirection.ASCENDING);
        Comparator<User> comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<User> users = this.userFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("department", SortDirection.DESCENDING);
        comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

    @Test
    public void test_success_sort_lastaccess() {
        QuerySortOrder querySortOrder = new QuerySortOrder("lastaccess", SortDirection.ASCENDING);
        Comparator<User> comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<User> users = this.userFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(users.get(0), users.get(1)));

        querySortOrder = new QuerySortOrder("lastaccess", SortDirection.DESCENDING);
        comparator = this.userFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(users.get(1), users.get(0)));
    }

}
