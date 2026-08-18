package org.ikasan.spec.solr;

import java.util.List;

/**
 * Created by Ikasan Development on 27/08/2017.
 */
public interface SolrGeneralService<ENTITY, RESULTS>
{
    /**
     * Save or update an ENTITY
     *
     * @param entity
     */
    void saveOrUpdate(ENTITY entity);

    /**
     * Save or update a list of ENTITY
     *
     * @param entity
     */
    void saveOrUpdate(List<ENTITY> entity);

    /**
     * Backs up the Solr index to a specified location with a specified number of backups to keep.
     *
     * @param backupLocationPath The path where the backup of the index should be stored
     * @param numberOfBackupsToKeep The number of backup copies of the index to keep
     */
    void backupIndex(String backupLocationPath, int numberOfBackupsToKeep);

    /**
     * Set the solr username
     *
     * @param solrUsername
     */
    void setSolrUsername(String solrUsername);


    /**
     * Set the solr password
     *
     * @param solrPassword
     */
    void setSolrPassword(String solrPassword);
}
