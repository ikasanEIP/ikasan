package org.ikasan.spec.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Ikasan Development Team on 14/02/2017.
 */
public abstract class SolrServiceBase
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrServiceBase.class);

    protected String solrUsername;
    protected String solrPassword;


    /**
     * Set the solr username
     *
     * @param solrUsername
     */
    public void setSolrUsername(String solrUsername)
    {
        this.solrUsername = solrUsername;
    }


    /**
     * Set the solr password
     *
     * @param solrPassword
     */
    public void setSolrPassword(String solrPassword)
    {
        this.solrPassword = solrPassword;
    }
}
