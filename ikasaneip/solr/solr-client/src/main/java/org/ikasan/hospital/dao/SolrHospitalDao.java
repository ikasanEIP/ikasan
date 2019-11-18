package org.ikasan.hospital.dao;

import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.systemevent.SystemEvent;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrHospitalDao extends SolrDaoBase<ExclusionEventAction>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrHospitalDao.class);

    /**
     * We need to give this hibernate it's context.
     */
    public static final String EXCLUSION_EVENT_ACTION = "exclusionEventAction";

    @Override
    protected SolrInputDocument getSolrInputFields(Long expiry, ExclusionEventAction exclusionEventAction)
    {

        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "" + exclusionEventAction.getErrorUri());
        document.addField(TYPE, EXCLUSION_EVENT_ACTION);
        document.addField(MODULE_NAME, exclusionEventAction.getModuleName());
        document.addField(FLOW_NAME, exclusionEventAction.getFlowName());
        document.addField(EVENT, exclusionEventAction.getAction());
        document.addField(PAYLOAD_CONTENT, exclusionEventAction.getEvent());
        document.addField(CREATED_DATE_TIME, exclusionEventAction.getTimestamp());
        document.setField(EXPIRY, expiry);

        return document;
    }


}
