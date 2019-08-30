package org.ikasan.solr.embedded;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class IkasanEmbeddedSolrAutoConfigurationTest
{
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(IkasanEmbeddedSolrAutoConfiguration.class));


    @Test
    @DirtiesContext
    public void test_autoconfigure_embedded_solr_server() throws Exception
    {
        contextRunner.withPropertyValues("solr.temp.dir=.")
            .run((context) ->
            {
                EmbeddedSolrServer embeddedSolrServer = (EmbeddedSolrServer)context.getBean("solrServer");

                assertEquals(0, embeddedSolrServer.query(new SolrQuery("*:*")).getResults().getNumFound());
                assertEquals(0, embeddedSolrServer.query("ikasan", new SolrQuery("*:*")).getResults().getNumFound());

                embeddedSolrServer.close();
            });
    }

}
