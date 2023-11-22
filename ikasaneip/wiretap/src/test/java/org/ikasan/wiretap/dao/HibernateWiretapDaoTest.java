/*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.wiretap.dao;

import javax.annotation.Resource;

import org.ikasan.WiretapAutoConfiguration;
import org.ikasan.WiretapTestAutoConfiguration;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapDao;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.WiretapFlowEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 * @author Ikasan Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WiretapAutoConfiguration.class, WiretapTestAutoConfiguration.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class HibernateWiretapDaoTest
{
	/** Object being tested */
	@Resource private WiretapDao wiretapDao;

	@Before
	public void setup() {

	    long timestamp = System.currentTimeMillis();

		for(int i=0; i<5000; i++)
		{
			WiretapFlowEvent event = new WiretapFlowEvent("moduleName" + i, "flowName" + i, "componentName" + i,
					"eventId" + i, "relatedEventId" + i, timestamp ,"event" + i, timestamp - 1000000000);

            timestamp++;

			this.wiretapDao.save(event);
		}

		List<WiretapEvent> events = new ArrayList<>();

        timestamp = System.currentTimeMillis() + 100000;

        for(int i=5000; i<10000; i++)
        {
            WiretapFlowEvent event = new WiretapFlowEvent("moduleName" + i, "flowName" + i, "componentName" + i,
                "eventId" + i, "relatedEventId" + i, timestamp ,"event" + i, timestamp - 1000000000);

            timestamp++;

            events.add(event);
        }

        this.wiretapDao.setHarvestQueryOrdered(false);
        this.wiretapDao.save(events);

	}

	@Test
	public void test_get_harvestable_records_and_update()
	{
	    List<WiretapEvent> events = wiretapDao.getHarvestableRecords(50);

		Assert.assertEquals("Wiretap events should equal!", events.size(), 50);

		wiretapDao.updateAsHarvested(events);

		events = wiretapDao.getHarvestableRecords(1000);
        wiretapDao.updateAsHarvested(events);

        events = wiretapDao.getHarvestableRecords(1000);
        wiretapDao.updateAsHarvested(events);


        events = wiretapDao.getHarvestableRecords(10000);

		Assert.assertEquals("Wiretap events should equal!", events.size(), 7950);
	}

    @Test
    public void test_get_harvestable_records_and_update_with_order_by()
    {
        List<WiretapEvent> events = wiretapDao.getHarvestableRecords(50);

        Assert.assertEquals("Wiretap events should equal!", events.size(), 50);

        wiretapDao.updateAsHarvested(events);

        events = wiretapDao.getHarvestableRecords(1000);
        wiretapDao.updateAsHarvested(events);

        events = wiretapDao.getHarvestableRecords(1000);
        wiretapDao.updateAsHarvested(events);


        events = wiretapDao.getHarvestableRecords(10000);

        Assert.assertEquals("Wiretap events should equal!", events.size(), 7950);
    }

    @Test
    public void test_get_harvestable_records_maintains_order_with_gap()
    {
        List<WiretapEvent> events = wiretapDao.getHarvestableRecords(3);

        Assert.assertEquals("Wiretap events should equal!", events.size(), 3);

        wiretapDao.updateAsHarvested(List.of(events.get(1)));

        events = wiretapDao.getHarvestableRecords(3);
        wiretapDao.updateAsHarvested(events);

        Assert.assertEquals("Identifier should equal!", events.get(0).getIdentifier(), 1);
        Assert.assertEquals("Identifier should equal!", events.get(1).getIdentifier(), 3);
        Assert.assertEquals("Identifier should equal!", events.get(2).getIdentifier(), 4);
    }

    @Test
    public void test_get_harvestable_records_maintains_order_with_gap_with_order_by()
    {
        this.wiretapDao.setHarvestQueryOrdered(true);
        List<WiretapEvent> events = wiretapDao.getHarvestableRecords(3);

        Assert.assertEquals("Wiretap events should equal!", events.size(), 3);

        wiretapDao.updateAsHarvested(List.of(events.get(1)));

        events = wiretapDao.getHarvestableRecords(3);
        wiretapDao.updateAsHarvested(events);

        Assert.assertEquals("Identifier should equal!", events.get(0).getIdentifier(), 1);
        Assert.assertEquals("Identifier should equal!", events.get(1).getIdentifier(), 3);
        Assert.assertEquals("Identifier should equal!", events.get(2).getIdentifier(), 4);
    }

    @Test
    public void test_housekeepables_exits()
    {
        boolean houserKeepablesExist = wiretapDao.housekeepablesExist();

        Assert.assertEquals("Housekeepables exist!",houserKeepablesExist, true);
    }

    @Test
    public void test_housekeep()
    {
        wiretapDao.setBatchHousekeepDelete(true);
        wiretapDao.deleteAllExpired();

        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 8000);
    }

    @Test
    public void test_housekeep_batch()
    {
        wiretapDao.setBatchHousekeepDelete(false);
        wiretapDao.deleteAllExpired();

        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 0);
    }

    @Test
    public void test_find_by_id()
    {
        List<WiretapEvent> events = wiretapDao.getHarvestableRecords(1);

        WiretapEvent event = this.wiretapDao.findById(events.get(0).getIdentifier());

        Assert.assertEquals("Wiretap event equals", event.getIdentifier(), events.get(0).getIdentifier());
    }

    @Test
    public void test_find_by_module_name_collection()
    {
        HashSet<String> moduleNames = new HashSet<>();
        moduleNames.add("moduleName1");
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, moduleNames,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 1);
    }

    @Test
    public void test_find_by_module_name_collection_null()
    {
        HashSet<String> moduleNames = null;
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, moduleNames,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 10000);
    }

    @Test
    public void test_find_by_module_name_collection_with_null_entry()
    {
        HashSet<String> moduleNames = new HashSet<>();
        moduleNames.add(null);
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, moduleNames,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 0);
    }

    @Test
    public void test_find_by_component_name_collection()
    {
        HashSet<String> componentNames = new HashSet<>();
        componentNames.add("componentName1");
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, componentNames, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 1);
    }

    @Test
    public void test_find_by_component_name()
    {
        HashSet<String> componentNames = new HashSet<>();
        componentNames.add("componentName1");
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            null, "componentName1", null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 1);
    }

    @Test
    public void test_find_by_component_name_empty()
    {
        HashSet<String> componentNames = new HashSet<>();
        componentNames.add("componentName1");
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            null, "", null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 10000);
    }

    @Test
    public void test_find_by_compenent_name_collection_null()
    {
        HashSet<String> componentNames = null;
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, componentNames, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 10000);
    }

    @Test
    public void test_find_by_component_name_collection_with_null_entry()
    {
        HashSet<String> componentNames = new HashSet<>();
        componentNames.add(null);
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, componentNames, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 0);
    }

    @Test
    public void test_find_by_flow_name_collection()
    {
        HashSet<String> flowNames = new HashSet<>();
        flowNames.add("flowName1");
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 1);
    }

    @Test
    public void test_find_by_flow_name()
    {
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            "flowName1", null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 1);
    }

    @Test
    public void test_find_by_flow_name_empty()
    {
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            "", null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 10000);
    }

    @Test
    public void test_find_by_flow_name_collection_null()
    {
        HashSet<String> flowNames = null;
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 10000);
    }

    @Test
    public void test_find_by_flow_name_collection_with_null_entry()
    {
        HashSet<String> flowNames = new HashSet<>();
        flowNames.add(null);
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 0);
    }

    @Test
    public void test_find_by_event_id()
    {
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, "eventId1", null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", 1, events.getResultSize());
    }

    @Test
    public void test_find_by_event()
    {
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, null, null, "event9999");

        Assert.assertEquals("Wiretap event result size == 1", 1,events.getResultSize());
    }

    @Test
    public void test_find_by_event_when_starts_with_percent()
    {
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, null, null, "%vent9999%");

        Assert.assertEquals("Wiretap event result size == 1",  1,events.getResultSize());
    }

    @Test
    public void test_find_by_date_time_range()
    {
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, new Date(System.currentTimeMillis()-100000000L), new Date(System.currentTimeMillis()+100000000L), null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 10000);
    }

    @Test
    public void test_find_by_date_time_range_nothing_found()
    {
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, null, false, null,
            flowNames, null, null, null, new Date(System.currentTimeMillis()+1000000L), new Date(System.currentTimeMillis()+2000000L), null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 0);
    }

    @Test
    public void test_order_by_asc()
    {
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, "timestamp", true, null,
            flowNames, null, null, null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 10000);
        Assert.assertEquals("Wiretap name equals", events.getPagedResults().get(0).getModuleName(), "moduleName0");
    }

    @Test
    public void test_order_by_desc()
    {
        HashSet<String> flowNames = new HashSet<>();
        PagedSearchResult<WiretapEvent> events = this.wiretapDao.findWiretapEvents(0, 1, "timestamp", false, null,
            flowNames, null, "eventId1", null, null, null, null);

        Assert.assertEquals("Wiretap event result size == 1", events.getResultSize(), 1);
        Assert.assertEquals("Wiretap name equals", events.getPagedResults().get(0).getModuleName(), "moduleName1");
    }
}
