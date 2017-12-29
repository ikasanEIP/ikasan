package org.ikasan.spec.solr;

import java.util.List;

/**
 * Created by Ikasan Development Team on 06/08/2017.
 */
public interface SolrService<ENTITY>
{
    /**
     * Method to save the solr entity.
     *
     * @param save
     */
    public void save(ENTITY save);

    /**
     * Method to save the solr entity.
     *
     * @param save
     */
    public void save(List<ENTITY> save);

    /**
     * Set the solr username
     *
     * @param solrUsername
     */
    public void setSolrUsername(String solrUsername);


    /**
     * Set the solr password
     *
     * @param solrPassword
     */
    public void setSolrPassword(String solrPassword);

}
