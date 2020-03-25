package org.ikasan.solr.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.ikasan.solr.dao.SolrGeneralDaoImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrGeneralSearchServiceTest extends SolrTestCaseJ4
{
    SolrGeneralDaoImpl dao;

    @Test(expected = IllegalArgumentException.class)
    @DirtiesContext
    public void test_constructor_dao_null_exception()
    {
        new SolrGeneralServiceImpl(null);
    }

    @Test
    @DirtiesContext
    public void test_search_success() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
                .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
                .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(dao);

            assertEquals(3, solrGeneralService.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_with_offset_success() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
            .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "blah");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(dao);

            assertEquals(1, solrGeneralService.search("test", 0, System.currentTimeMillis() + 100000000l, 1,100, null).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_entity_types_success() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
                .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
                .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(dao);

            ArrayList<String> entityTypes = new ArrayList<>();
            entityTypes.add("type");

            assertEquals(3, solrGeneralService.search(null, null, "test", 0, System.currentTimeMillis() + 100000000l, 100, entityTypes)
                    .getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_entity_types_no_module_or_flow_success() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
            .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(dao);

            ArrayList<String> entityTypes = new ArrayList<>();
            entityTypes.add("test");

            assertEquals(3, solrGeneralService.search("test", 0, System.currentTimeMillis() + 100000000l, 100, entityTypes)
                .getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_search_success_with_query_filter() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
                .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
                .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", "1");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("moduleName", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "2");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("moduleName", "test");
            doc.addField("expiry", 100l);
            doc.addField("timestamp", 100l);
            server.add("ikasan", doc);
            doc = new SolrInputDocument();
            doc.addField("id", "3");
            doc.addField("type", "type");
            doc.addField("payload", "test");
            doc.addField("moduleName", "test");
            doc.addField("timestamp", 100l);
            doc.addField("expiry", System.currentTimeMillis() + 10000000l);
            server.add("ikasan", doc);
            server.commit();

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            Set<String> moduleNames = new HashSet<String>();
            moduleNames.add("test");

            SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(dao);

            assertEquals(3, solrGeneralService.search(moduleNames, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_save_document() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
            .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            IkasanSolrDocument doc = new IkasanSolrDocument();
            doc.setModuleName("test");
            doc.setExpiry(100l);
            doc.setTimeStamp(100l);
            doc.setEvent("test");
            doc.setId("1");

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            Set<String> moduleNames = new HashSet<String>();
            moduleNames.add("test");

            SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(dao);
            solrGeneralService.saveOrUpdate(doc);


            assertEquals(1, solrGeneralService.search(moduleNames, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    @Test
    @DirtiesContext
    public void test_save_documents() throws Exception {
        Path path = createTempDir();

        SolrResourceLoader loader = new SolrResourceLoader(path);
        NodeConfig config = new NodeConfig.NodeConfigBuilder("testnode", loader)
            .setConfigSetBaseDirectory(Paths.get(TEST_HOME()).resolve("configsets").toString())
            .build();

        try (EmbeddedSolrServer server = new EmbeddedSolrServer(config, "ikasan"))
        {
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName("ikasan");
            createRequest.setConfigSet("minimal");
            server.request(createRequest);

            IkasanSolrDocument doc1 = new IkasanSolrDocument();
            doc1.setModuleName("test");
            doc1.setId("1");
            doc1.setEvent("test");
            doc1.setExpiry(100l);
            doc1.setTimeStamp(100l);

            IkasanSolrDocument doc2 = new IkasanSolrDocument();
            doc2.setModuleName("test");
            doc2.setId("2");
            doc2.setEvent("test");
            doc2.setExpiry(100l);
            doc2.setTimeStamp(100l);

            dao = new SolrGeneralDaoImpl();
            dao.setSolrClient(server);

            Set<String> moduleNames = new HashSet<String>();
            moduleNames.add("test");

            List<IkasanSolrDocument> documents = new ArrayList<>();
            documents.add(doc1);
            documents.add(doc2);

            SolrGeneralServiceImpl solrGeneralService = new SolrGeneralServiceImpl(dao);
            solrGeneralService.saveOrUpdate(documents);

            IkasanSolrDocumentSearchResults results = solrGeneralService.search(moduleNames, null, "test", 0, System.currentTimeMillis() + 100000000l, 100);

            assertEquals(2, solrGeneralService.search(moduleNames, null, "test", 0, System.currentTimeMillis() + 100000000l, 100).getResultList().size());

        }
    }

    public static String TEST_HOME() {
        return getFile("solr/ikasan").getParent();
    }

    public static Path TEST_PATH() {
        return getFile("solr/ikasan").getParentFile().toPath();
    }


}
