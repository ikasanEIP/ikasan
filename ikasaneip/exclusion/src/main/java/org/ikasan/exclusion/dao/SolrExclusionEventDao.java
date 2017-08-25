package org.ikasan.exclusion.dao;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.spec.solr.SolrDaoBase;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Ikasan Development Team on 05/08/2017.
 */
public class SolrExclusionEventDao extends SolrDaoBase implements ExclusionEventDao<String, ExclusionEvent>
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(SolrExclusionEventDao.class);

    /**
     * We need to give this dao it's context.
     */
    public static final String EXCLUSION = "exclusion";

    @Override
    public void save(ExclusionEvent exclusionEvent)
    {
        long millisecondsInDay = (this.daysToKeep * TimeUnit.DAYS.toMillis(1));
        long expiry = millisecondsInDay + System.currentTimeMillis();

        SolrInputDocument document = new SolrInputDocument();
        document.addField(ID, "" + exclusionEvent.getErrorUri());
        document.addField(TYPE, EXCLUSION);
        document.addField(MODULE_NAME, exclusionEvent.getModuleName());
        document.addField(FLOW_NAME, exclusionEvent.getFlowName());
        document.addField(EVENT, exclusionEvent.getIdentifier());
        document.addField(PAYLOAD_CONTENT, new String(exclusionEvent.getEvent()));
        document.addField(CREATED_DATE_TIME, exclusionEvent.getTimestamp());
        document.setField(EXPIRY, expiry);

        try
        {
            logger.debug("Adding document: " + document);
            solrClient.add(document);
            solrClient.commit();
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception has occurred attempting to write an exclusion to Solr", e);
        }
    }

    @Override
    public void delete(String moduleName, String flowName, String s)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String errorUri)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExclusionEvent find(String moduleName, String flowName, String s)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long rowCount(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, String identifier)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ExclusionEvent> findAll()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ExclusionEvent> find(List<String> moduleName, List<String> flowName, Date starteDate, Date endDate, String s, int size)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExclusionEvent find(String errorUri)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ExclusionEvent> getHarvestableRecords(int housekeepingBatchSize)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteAllExpired()
    {
        super.removeExpired(EXCLUSION);
    }

}
