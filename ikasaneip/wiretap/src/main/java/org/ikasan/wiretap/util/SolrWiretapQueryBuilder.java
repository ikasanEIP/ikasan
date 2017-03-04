package org.ikasan.wiretap.util;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrWiretapQueryBuilder extends SolrQueryBuilder
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(SolrWiretapQueryBuilder.class);

    public static final String MODULE_NAME = "ModuleName:";
    public static final String FLOW_NAME = "FlowName:";
    public static final String COMPONENT_NAME = "ComponentName:";
    public static final String CREATED_DATE_TIME = "CreatedDateTime:";
    public static final String PAYLOAD_CONTENT = "PayloadContent:";

    /**
     * Method to build solr wiretap queries.
     * 
     * @param moduleNames
     * @param flowNames
     * @param componentNames
     * @param fromDate
     * @param untilDate
     * @param payloadContent
     * @return
     */
    public String buildQuery(Collection<String> moduleNames, Collection<String> flowNames, Collection<String> componentNames, Date fromDate, Date untilDate, String payloadContent)
    {
        StringBuffer moduleNamesBuffer = null;
        StringBuffer flowNamesBuffer = null;
        StringBuffer componentNamesBuffer = null;
        StringBuffer dateBuffer = null;
        StringBuffer payloadBuffer = null;

        if(moduleNames != null && moduleNames.size() > 0)
        {
            moduleNamesBuffer = this.createCompositeQuery(moduleNames, MODULE_NAME, OR);
        }

        if(flowNames != null && flowNames.size() > 0)
        {
            flowNamesBuffer = this.createCompositeQuery(flowNames, FLOW_NAME, OR);
        }

        if(componentNames != null)
        {
            componentNamesBuffer = this.createCompositeQuery(componentNames, COMPONENT_NAME, OR);
        }

        if(payloadContent != null)
        {
            payloadBuffer = this.createCompositeQuery(Arrays.asList(payloadContent), PAYLOAD_CONTENT, AND);
        }

        dateBuffer = this.createDateBetweenQuery(fromDate, untilDate, CREATED_DATE_TIME);

        StringBuffer bufferFinalQuery = new StringBuffer();

        bufferFinalQuery.append(this.buildFinalQuery(moduleNamesBuffer, bufferFinalQuery.length() > 0));
        bufferFinalQuery.append(this.buildFinalQuery(flowNamesBuffer, bufferFinalQuery.length() > 0));
        bufferFinalQuery.append(this.buildFinalQuery(componentNamesBuffer, bufferFinalQuery.length() > 0));
        bufferFinalQuery.append(this.buildFinalQuery(payloadBuffer, bufferFinalQuery.length() > 0));
        bufferFinalQuery.append(this.buildFinalQuery(dateBuffer, bufferFinalQuery.length() > 0));

        if(bufferFinalQuery.length() == 0)
        {
            bufferFinalQuery = super.createOpenQuery();
        }

        return bufferFinalQuery.toString();
    }
}
