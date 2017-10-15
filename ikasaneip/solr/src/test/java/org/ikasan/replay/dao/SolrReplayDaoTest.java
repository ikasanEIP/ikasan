package org.ikasan.replay.dao;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.spec.replay.ReplayEvent;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Ikasan Development on 29/08/2017.
 */
public class SolrReplayDaoTest extends SolrTestCaseJ4
{

    @Test
    public void test_query_replay_events() throws Exception
    {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
                .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
                .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "newcore"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("newcore");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);


            SolrReplayDao dao = new SolrReplayDao();
            dao.setSolrClient(server);

            List<ReplayEvent> replayEventList = dao.getReplayEvents(new ArrayList<>(), new ArrayList<>()
                , null,null, new Date(System.currentTimeMillis() - 10000000l), new Date());

            assertEquals(0, replayEventList.size());

            server.close();
        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }
}
