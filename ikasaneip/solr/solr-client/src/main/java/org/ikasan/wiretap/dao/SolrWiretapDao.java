package org.ikasan.wiretap.dao;

import org.apache.solr.common.SolrInputDocument;
import org.ikasan.spec.solr.SolrDaoBase;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public class SolrWiretapDao extends SolrDaoBase<WiretapEvent>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrWiretapDao.class);

    /**
     * We need to give this hibernate it's context.
     */
    public static final String WIRETAP = "wiretap";


    @Override
    protected SolrInputDocument convertEntityToSolrInputDocument(Long expiry, WiretapEvent wiretapEvent)
    {
        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, wiretapEvent.getModuleName() + "-wiretap-"
            + wiretapEvent.getIdentifier());
        document.addField(TYPE, WIRETAP);
        document.addField(MODULE_NAME, wiretapEvent.getModuleName());
        document.addField(FLOW_NAME, wiretapEvent.getFlowName());
        document.addField(COMPONENT_NAME, wiretapEvent.getComponentName());
        document.addField(EVENT, wiretapEvent.getEventId());
        document.addField(PAYLOAD_CONTENT, wiretapEvent.getEvent());
        document.addField(CREATED_DATE_TIME, wiretapEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        return document;
    }

}
