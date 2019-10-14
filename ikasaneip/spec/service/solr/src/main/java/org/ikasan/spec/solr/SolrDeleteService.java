package org.ikasan.spec.solr;

public interface SolrDeleteService
{
    /**
     * Method to remove records from the solr index by type and id.
     *
     * @param type
     */
    public void removeById(String type, String id);
}
