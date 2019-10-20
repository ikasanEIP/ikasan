package org.ikasan.hospital.dao;

import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.spec.hospital.model.ExclusionEventAction;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrHospitalDao extends SolrDaoBase
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrHospitalDao.class);

    /**
     * We need to give this hibernate it's context.
     */
    public static final String EXCLUSION_EVENT_ACTION = "exclusionEventAction";

    public void save(ExclusionEventAction exclusionEventAction)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "" + exclusionEventAction.getErrorUri());
        document.addField(TYPE, EXCLUSION_EVENT_ACTION);
        document.addField(MODULE_NAME, exclusionEventAction.getModuleName());
        document.addField(FLOW_NAME, exclusionEventAction.getFlowName());
        document.addField(EVENT, exclusionEventAction.getAction());
        document.addField(PAYLOAD_CONTENT, exclusionEventAction.getEvent());
        document.addField(CREATED_DATE_TIME, exclusionEventAction.getTimestamp());
        document.setField(EXPIRY, expiry);

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            req.add(document);

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            logger.debug("Adding document: " + document + ". Response: " + rsp.toString());

            req.commit(solrClient, SolrConstants.CORE);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write a persistence to Solr", e);
        }
    }

    public void save(List<ExclusionEventAction> exclusionEventActions)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            for(ExclusionEventAction exclusionEventAction: exclusionEventActions)
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

                req.add(document);

                logger.debug("Adding document: " + document);
            }

            UpdateResponse rsp = req.process(this.solrClient, SolrConstants.CORE);

            logger.debug("Solr Response: " + rsp.toString());

            req.commit(solrClient, SolrConstants.CORE);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write a persistence to Solr", e);
        }
    }
}
