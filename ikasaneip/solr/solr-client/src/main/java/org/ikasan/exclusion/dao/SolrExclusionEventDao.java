package org.ikasan.exclusion.dao;

import org.apache.solr.common.SolrInputDocument;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ikasan Development Team on 05/08/2017.
 */
public class SolrExclusionEventDao extends SolrDaoBase<ExclusionEvent>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrExclusionEventDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String EXCLUSION = "exclusion";

    @Override
    protected SolrInputDocument convertEntityToSolrInputDocument(Long expiry, ExclusionEvent exclusionEvent)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, exclusionEvent.getModuleName() + ":" + "exclusion" + ":" + exclusionEvent.getErrorUri());
        document.addField(TYPE, EXCLUSION);
        document.addField(MODULE_NAME, exclusionEvent.getModuleName());
        document.addField(ERROR_URI, exclusionEvent.getErrorUri());
        document.addField(FLOW_NAME, exclusionEvent.getFlowName());
        document.addField(EVENT, exclusionEvent.getIdentifier());
        document.addField(PAYLOAD_CONTENT, new String(exclusionEvent.getEvent()));
        document.addField(CREATED_DATE_TIME, exclusionEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        return document;
    }
}
