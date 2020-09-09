package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@DirtiesContext
public class FilteringGridTest {

    private FilteringGrid<MyEntity> filteringGrid;
    private MyFilter myFilter;

    @Before
    public void setup() {
        myFilter = new MyFilter();
        filteringGrid = new FilteringGrid<>(myFilter);
        filteringGrid.addColumn(MyEntity::getName).setKey("name").setHeader("Name").setSortable(true);
        filteringGrid.addColumn(MyEntity::getDescription).setKey("description").setHeader("Description").setSortable(true);
        filteringGrid.addColumn(MyEntity::getSomeOtherValue).setKey("otherValue").setHeader("Description").setSortable(true);

        HeaderRow hr = filteringGrid.appendHeaderRow();
        filteringGrid.addGridFiltering(hr, myFilter::setNameFilter, "name");
        filteringGrid.addGridFiltering(hr, myFilter::setDescriptionFilter, "description");
        filteringGrid.addGridFiltering(hr, myFilter::setSomeOtherValueFilter, "otherValue");

        List<MyEntity> myEntities = new ArrayList<>();

        for(int i=0; i<100; i++) {
            myEntities.add(new MyEntity("name"+i, "otherValue"+i, "description"+i));
        }

        filteringGrid.setItems(myEntities);
    }

    @Test
    public void test_success_not_filtered() {
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
    }

    @Test
    public void test_success_filtered_description() {
        this.myFilter.setDescriptionFilter("99");
        Assert.assertEquals(1, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setDescriptionFilter("9");
        Assert.assertEquals(19, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setDescriptionFilter("description");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setDescriptionFilter("descr");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setDescriptionFilter("tion");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
    }

    @Test
    public void test_success_filtered_name() {
        this.myFilter.setNameFilter("99");
        Assert.assertEquals(1, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setNameFilter("9");
        Assert.assertEquals(19, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setNameFilter("name");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setNameFilter("na");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setNameFilter("me");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
    }

    @Test
    public void test_success_filtered_other_value() {
        this.myFilter.setSomeOtherValueFilter("99");
        Assert.assertEquals(1, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setSomeOtherValueFilter("9");
        Assert.assertEquals(19, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setSomeOtherValueFilter("otherValue");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setSomeOtherValueFilter("other");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
        this.myFilter.setSomeOtherValueFilter("value");
        Assert.assertEquals(100, filteringGrid.getDataProvider().size(new Query<>()));
    }

    private class MyEntity {
        private String name;
        private String someOtherValue;
        private String description;

        public MyEntity(String name, String someOtherValue, String description) {
            this.name = name;
            this.someOtherValue = someOtherValue;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getSomeOtherValue() {
            return someOtherValue;
        }

        public String getDescription() {
            return description;
        }
    }

    private class MyFilter implements Filter<MyEntity> {

        private Collection<MyEntity> myEntities;
        private String nameFilter = null;
        private String someOtherValueFilter = null;
        private String descriptionFilter = null;

        @Override
        public void setItems(Collection<MyEntity> myEntities) {
            this.myEntities = myEntities;
        }

        @Override
        public Collection<MyEntity> getItems() {
            return this.myEntities;
        }

        @Override
        public Stream<MyEntity> getFilterStream() {
            return myEntities
                .stream()
                .filter(myEntity ->
                {
                    if(this.getNameFilter() == null || this.getNameFilter().isEmpty())
                    {
                        return true;
                    }
                    else if(myEntity.getName() == null)
                    {
                        return false;
                    }
                    else
                    {
                        return myEntity.getName().toLowerCase().contains(getNameFilter().toLowerCase());
                    }
                })
                .filter(myEntity ->
                {
                    if(this.descriptionFilter == null || this.descriptionFilter.isEmpty())
                    {
                        return true;
                    }
                    else if(myEntity.getDescription() == null)
                    {
                        return false;
                    }
                    else
                    {
                        return myEntity.getDescription().toLowerCase().contains(getDescriptionFilter().toLowerCase());
                    }
                })
                .filter(myEntity ->
                {
                    if(this.someOtherValueFilter == null || this.someOtherValueFilter.isEmpty())
                    {
                        return true;
                    }
                    else if(myEntity.getDescription() == null)
                    {
                        return false;
                    }
                    else
                    {
                        return myEntity.getSomeOtherValue().toLowerCase().contains(getSomeOtherValueFilter().toLowerCase());
                    }
                });
        }

        @Override
        public Comparator getSortComparator(List<QuerySortOrder> querySortOrders) {
            Comparator comparator = null;

            if(querySortOrders.get(0).getSorted().equals("name"))
            {
                comparator =  Comparator.comparing(MyEntity::getName, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            }
            else if(querySortOrders.get(0).getSorted().equals("description"))
            {
                comparator = Comparator.comparing(MyEntity::getDescription, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            }
            else if(querySortOrders.get(0).getSorted().equals("otherValue"))
            {
                comparator = Comparator.comparing(MyEntity::getSomeOtherValue, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER));
            }

            return comparator;
        }

        public String getNameFilter() {
            return nameFilter;
        }

        public void setNameFilter(String nameFilter) {
            this.nameFilter = nameFilter;
        }

        public String getSomeOtherValueFilter() {
            return someOtherValueFilter;
        }

        public void setSomeOtherValueFilter(String someOtherValueFilter) {
            this.someOtherValueFilter = someOtherValueFilter;
        }

        public String getDescriptionFilter() {
            return descriptionFilter;
        }

        public void setDescriptionFilter(String descriptionFilter) {
            this.descriptionFilter = descriptionFilter;
        }
    }
}
