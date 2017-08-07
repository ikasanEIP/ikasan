package org.ikasan.spec.solr;

/**
 * Created by stewmi on 06/08/2017.
 */
public interface SolrService<ENTITY>
{
    /**
     * Method to save the solr entity.
     *
     * @param save
     */
    public void save(ENTITY save);
}
