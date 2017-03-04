package org.ikasan.wiretap.util;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stewmi on 27/02/2017.
 */
public class SolrWiretapQueryBuilderTest
{
    private ArrayList<String> moduleNames = new ArrayList<>();
    private ArrayList<String> flowNames = new ArrayList<>();
    private ArrayList<String> componentNames = new ArrayList<>();

    private Date from = new Date(100000000L);
    private Date to = new Date(200000000L);

    private String payloadContent = "payloadContent";

    @Before
    public void setup()
    {
        moduleNames.add("moduleName1");
        moduleNames.add("moduleName2");

        flowNames.add("flowName1");
        flowNames.add("flowName2");

        componentNames.add("componentName1");
        componentNames.add("componentName2");
    }

    @Test
    public void test_success()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, flowNames, componentNames, from, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_null_module_names()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(null, flowNames, componentNames, from, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_empty_module_names()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(new ArrayList<String>(), flowNames, componentNames, from, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_null_flow_names()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, null, componentNames, from, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_empty_flow_names()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, new ArrayList<String>(), componentNames, from, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_null_component_names()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, flowNames, null, from, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_empty_component_names()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, flowNames, new ArrayList<String>(), from, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_from_date_null()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, flowNames, componentNames, null, to, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_to_date_null()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, flowNames, componentNames, from, null, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_both_dates_null()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, flowNames, componentNames, null, null, payloadContent);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_payload_content_null()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(moduleNames, flowNames, componentNames, from, to, null);

        System.out.println("Query: " + query);
    }

    @Test
    public void test_success_all_null()
    {
        SolrWiretapQueryBuilder builder = new SolrWiretapQueryBuilder();

        String query = builder.buildQuery(null, null, null, null, null, null);

        System.out.println("Query: " + query);
    }
}
