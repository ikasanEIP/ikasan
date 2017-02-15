package org.ikasan.wiretap.dao;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.wiretap.model.ArrayListPagedSearchResult;
import org.ikasan.wiretap.model.SolrWiretapEvent;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by stewmi on 14/02/2017.
 */
public class SolrWiretapDao //implements WiretapDao
{


    public static final void main(String[] args)
    {
        SolrClient client = new HttpSolrClient.Builder("http://adl-cmi20:8983/solr/db").build();

        for(int i=0; i< 500; i++)
        {
            SolrQuery query = new SolrQuery();
            query.setQuery("PayloadContent:cmfAsset");
            query.setStart(i * 20);
            query.setRows(20);

            try
            {
                long start= System.currentTimeMillis();
                QueryResponse rsp = client.query( query );

                SolrDocumentList docs = rsp.getResults();

                List<SolrWiretapEvent> beans = rsp.getBeans(SolrWiretapEvent.class);

                for(SolrWiretapEvent event: beans)
                {
                    System.out.println("Page " + i + "time: " + (System.currentTimeMillis() - start));

                    System.out.println(event.getEvent());
                }
            }
            catch (SolrServerException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }
}
