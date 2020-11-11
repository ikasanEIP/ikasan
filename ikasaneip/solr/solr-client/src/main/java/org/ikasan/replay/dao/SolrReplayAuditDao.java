package org.ikasan.replay.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.replay.model.SolrReplayAuditEvent;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.solr.SolrConstants;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SolrReplayAuditDao extends SolrDaoBase<SolrReplayAuditEvent> implements BatchInsert<SolrReplayAuditEvent>
{
    private static Logger logger = LoggerFactory.getLogger(SolrReplayAuditDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String REPLAY_AUDIT = "replay_audit";

    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void insert(List<SolrReplayAuditEvent> entities)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        try
        {
            UpdateRequest req = new UpdateRequest();
            req.setBasicAuthCredentials(this.solrUsername, this.solrPassword);

            String json = mapper.writeValueAsString(entities);

            logger.debug("Result json: " + json);

            SolrInputDocument document = new SolrInputDocument();
            document.addField(ID, "replay-audit-" + System.currentTimeMillis());
            document.addField(TYPE, REPLAY_AUDIT);
            document.addField(PAYLOAD_CONTENT, json);
            document.addField(CREATED_DATE_TIME, System.currentTimeMillis());
            document.setField(EXPIRY, expiry);

            req.add(document);

            logger.debug("Adding document: " + document);


            commitSolrRequest(req);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write replay audit event to Solr", e);
        }
    }

    @Override
    protected SolrInputDocument getSolrInputFields(Long expiry, SolrReplayAuditEvent event)
    {
        SolrInputDocument document = new SolrInputDocument();

        try
        {
            String json = mapper.writeValueAsString(event);

            logger.debug("Result json: " + json);

            document.addField(ID, "replay-audit-" + System.currentTimeMillis());
            document.addField(TYPE, REPLAY_AUDIT);
            document.addField(PAYLOAD_CONTENT, json);
            document.addField(CREATED_DATE_TIME, System.currentTimeMillis());
            document.setField(EXPIRY, expiry);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred convert a replay audit event to a solr document", e);
        }

        return document;
    }
}
