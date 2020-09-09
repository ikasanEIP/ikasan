package org.ikasan.dashboard.ui.administration.filter;

import com.google.common.collect.Lists;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.topology.metadata.model.ModuleMetaDataImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleFilterTest {
    private ModuleFilter moduleFilter;

    @Before
    public void setup() {

        List<ModuleMetaData> modules = new ArrayList<>();

        for(int i=0; i<100; i++) {
            ModuleMetaData moduleMetaData = new ModuleMetaDataImpl();
            moduleMetaData.setName("name"+i);

            modules.add(moduleMetaData);
        }

        moduleFilter = new ModuleFilter();
        moduleFilter.setItems(modules);
    }

    @Test
    public void test_success_no_filter() {
        Assert.assertEquals(100, moduleFilter.getFilterStream().collect(Collectors.toList()).size());
    }

    @Test
    public void test_success_filter_name() {
        this.moduleFilter.setModuleNameFilter("name");
        Assert.assertEquals(100, moduleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.moduleFilter.setModuleNameFilter("na");
        Assert.assertEquals(100, moduleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.moduleFilter.setModuleNameFilter("ame");
        Assert.assertEquals(100, moduleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.moduleFilter.setModuleNameFilter("ame9");
        Assert.assertEquals(11, moduleFilter.getFilterStream().collect(Collectors.toList()).size());
        this.moduleFilter.setModuleNameFilter("bad-name");
        Assert.assertEquals(0, moduleFilter.getFilterStream().collect(Collectors.toList()).size());
    }


    @Test
    public void test_success_sort_name() {
        QuerySortOrder querySortOrder = new QuerySortOrder("name", SortDirection.ASCENDING);
        Comparator<ModuleMetaData> comparator = this.moduleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        List<ModuleMetaData> moduleMetaData = this.moduleFilter.getItems().stream().collect(Collectors.toList());

        Assert.assertEquals(-1, comparator.compare(moduleMetaData.get(0), moduleMetaData.get(1)));

        querySortOrder = new QuerySortOrder("name", SortDirection.DESCENDING);
        comparator = this.moduleFilter.getSortComparator(Lists.newArrayList(querySortOrder));

        Assert.assertEquals(1, comparator.compare(moduleMetaData.get(1), moduleMetaData.get(0)));
    }


}
