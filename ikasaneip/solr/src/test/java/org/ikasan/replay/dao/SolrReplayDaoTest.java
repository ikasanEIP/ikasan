package org.ikasan.replay.dao;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stewmi on 29/08/2017.
 */
@Ignore
// todo make me a real test
public class SolrReplayDaoTest
{
    @Test

    public void test()
    {
        ArrayList<String> servers = new ArrayList<>();
        servers.add("http://adl-cmi20:7574/solr");
        servers.add("http://adl-cmi20:8983/solr");

        SolrReplayDao dao = new SolrReplayDao();
        dao.init(servers, 7);

        dao.getReplayEvents(new ArrayList<>(), new ArrayList<>(), null,null, new Date(System.currentTimeMillis() - 10000000l), new Date());
    }
}
