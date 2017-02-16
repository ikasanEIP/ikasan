package org.ikasan.wiretap.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.ArrayListPagedSearchResult;
import org.ikasan.wiretap.model.SolrWiretapEvent;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by stewmi on 14/02/2017.
 */
public class SolrWiretapDao implements WiretapDao
{
    public static final String MODULE_NAME = "ModuleName:";
    public static final String FLOW_NAME = "FlowName:";
    public static final String COMPONENT_NAME = "ComponentName:";
    public static final String CREATED_DATE_TIME = "CreatedDateTime:";
    public static final String PAYLOAD_CONTENT = "PayloadContent:";

    public static final String AND = "AND";
    public static final String OR = "OR ";

    public static final String OPEN_BRACKET = "(";
    public static final String CLOSE_BRACKET = ")";

    private SolrClient client = new HttpSolrClient.Builder("http://adl-cmi20:8983/solr/db").build();

    @Override
    public void save(WiretapEvent wiretapEvent)
    {

    }

    @Override
    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, Set<String> moduleNames, String moduleFlow, String componentName, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent)
    {
        PagedSearchResult results = null;

        HashSet<String> flowNames = new HashSet<String>();
        if(moduleFlow != null && moduleFlow.length() > 0)
        {
            flowNames.add(moduleFlow);
        }

        HashSet<String> componentNames = new HashSet<String>();
        if(componentName != null && componentName.length() > 0)
        {
            componentNames.add(moduleFlow);
        }

        String queryString = this.buildQuery(moduleNames, flowNames, componentNames, fromDate, untilDate, payloadContent);

        System.out.println("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setStart(pageNo * pageSize);
        query.setRows(pageSize);
        query.setSort("CreatedDateTime", SolrQuery.ORDER.desc);

        try
        {
            QueryResponse rsp = client.query( query );

            List<SolrWiretapEvent> beans = rsp.getBeans(SolrWiretapEvent.class);

            results = new ArrayListPagedSearchResult(beans, beans.size(), beans.size());
        }
        catch (SolrServerException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    @Override
    public PagedSearchResult<WiretapEvent> findWiretapEvents(int pageNo, int pageSize, String orderBy, boolean orderAscending, Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, String eventId, String payloadId, Date fromDate, Date untilDate, String payloadContent)
    {
        PagedSearchResult results = null;

        String queryString = this.buildQuery(moduleNames, flowNames, componentNames, fromDate, untilDate, payloadContent);

        System.out.println("queryString: " + queryString);

        SolrQuery query = new SolrQuery();
        query.setQuery(queryString);
        query.setStart(pageNo * pageSize);
        query.setRows(pageSize);
        query.setSort("CreatedDateTime", SolrQuery.ORDER.desc);

        try
        {
            QueryResponse rsp = client.query( query );

            List<SolrWiretapEvent> beans = rsp.getBeans(SolrWiretapEvent.class);

            results = new ArrayListPagedSearchResult(beans, beans.size(), beans.size());
        }
        catch (SolrServerException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return results;
    }

    private String buildQuery(Set<String> moduleNames, Set<String> flowNames, Set<String> componentNames, Date fromDate, Date untilDate, String payloadContent)
    {
        StringBuffer moduleNamesBuffer = new StringBuffer();
        StringBuffer flowNamesBuffer = new StringBuffer();
        StringBuffer componentNamesBuffer = new StringBuffer();
        StringBuffer dateBuffer = new StringBuffer();
        StringBuffer payloadBuffer = new StringBuffer();

        String delim = "";

        if(moduleNames != null && moduleNames.size() > 0)
        {
            moduleNamesBuffer.append(MODULE_NAME);

            moduleNamesBuffer.append(OPEN_BRACKET);

            for (String moduleName : moduleNames)
            {
                moduleNamesBuffer.append(delim).append("\"").append(moduleName).append("\" ");
                delim = OR;
            }

            moduleNamesBuffer.append(CLOSE_BRACKET);
        }

        if(flowNames != null && flowNames.size() > 0)
        {
            delim = "";

            flowNamesBuffer.append(FLOW_NAME);

            flowNamesBuffer.append(OPEN_BRACKET);

            for (String moduleFlowName : flowNames)
            {
                flowNamesBuffer.append(delim).append("\"").append(moduleFlowName).append("\" ");
                delim = OR;
            }

            flowNamesBuffer.append(CLOSE_BRACKET);
        }

        if(componentNames != null)
        {
            delim = "";

            componentNamesBuffer.append(COMPONENT_NAME);

            componentNamesBuffer.append(OPEN_BRACKET);

            for (String componentName : componentNames)
            {
                componentNamesBuffer.append(delim).append("\"").append(componentName).append("\" ");
                delim = OR;
            }

            componentNamesBuffer.append(CLOSE_BRACKET);
        }

        if(fromDate != null && untilDate != null)
        {
            dateBuffer.append(CREATED_DATE_TIME).append("[").append(fromDate.getTime()).append(" TO ").append(untilDate.getTime()).append("]");
        }

        if(payloadContent != null)
        {
            payloadBuffer.append(PAYLOAD_CONTENT).append("").append(payloadContent).append("");

        }

        StringBuffer bufferFinalQuery = new StringBuffer();

        boolean hasPrevious = false;

        if(moduleNames != null && moduleNames.size() > 0)
        {
            bufferFinalQuery.append(moduleNamesBuffer);
            hasPrevious = true;
        }

        if(flowNames != null && flowNames.size() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(" ").append(AND).append(" ");
            }

            bufferFinalQuery.append(flowNamesBuffer);
            hasPrevious = true;
        }

        if(componentNames != null && componentNames.size() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(" ").append(AND).append(" ");
            }

            bufferFinalQuery.append(componentNamesBuffer);
            hasPrevious = true;
        }

        if(payloadContent != null && payloadContent.length() > 0)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(" ").append(AND).append(" ");
            }

            bufferFinalQuery.append(payloadBuffer);
            hasPrevious = true;
        }

        if(fromDate != null && untilDate != null)
        {
            if(hasPrevious)
            {
                bufferFinalQuery.append(" ").append(AND).append(" ");
            }

            bufferFinalQuery.append(dateBuffer);
        }

        return bufferFinalQuery.toString();
    }

    @Override
    public WiretapEvent findById(Long id)
    {
        return null;
    }

    @Override
    public void deleteAllExpired()
    {

    }

    @Override
    public boolean isBatchHousekeepDelete()
    {
        return false;
    }

    @Override
    public void setBatchHousekeepDelete(boolean batchHousekeepDelete)
    {

    }

    @Override
    public Integer getHousekeepingBatchSize()
    {
        return null;
    }

    @Override
    public void setHousekeepingBatchSize(Integer housekeepingBatchSize)
    {

    }

    @Override
    public Integer getTransactionBatchSize()
    {
        return null;
    }

    @Override
    public void setTransactionBatchSize(Integer transactionBatchSize)
    {

    }

    @Override
    public boolean housekeepablesExist()
    {
        return false;
    }

    @Override
    public void setHousekeepQuery(String housekeepQuery)
    {

    }

    public static final void main(String[] args)
    {
        SolrWiretapDao dao = new SolrWiretapDao();

        HashSet<String> moduleNames = new HashSet<>();
        moduleNames.add("frontArena-trade");
        moduleNames.add("goldenSource-referenceData");
        moduleNames.add("cdw-asset");

        HashSet<String> flowNames = new HashSet<>();
        flowNames.add("Bulk GsEsbApprovedSecurity Transformer Flow");

        HashSet<String> componentNames = new HashSet<>();
        componentNames.add("after Approved Security Producer");

        PagedSearchResult<WiretapEvent> results = dao.findWiretapEvents(0, 2000, null, false, moduleNames, flowNames, componentNames, null, null, new Date(1487041280791L - 1000000000l), new Date(1487041280791L + 10000000000000l), "100001555237");

//        PagedSearchResult<WiretapEvent> results = dao.findWiretapEvents(0, 2000, null, false, moduleNames, flowNames, componentNames, null, null, null, null, "100001555237");

        System.out.println("results: " + results);

        for(WiretapEvent event: results.getPagedResults())
        {
            System.out.println(event);
        }

        System.out.println("results: " + results.getResultSize());
    }
}
