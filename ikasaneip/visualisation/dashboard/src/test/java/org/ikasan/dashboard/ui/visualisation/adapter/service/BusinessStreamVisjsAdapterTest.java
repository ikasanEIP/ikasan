package org.ikasan.dashboard.ui.visualisation.adapter.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.ikasan.dashboard.ui.visualisation.adapter.model.*;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public class BusinessStreamVisjsAdapterTest
{
    @Before
    public void setup()
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);
    }

    @Test
    public void test() throws IOException
    {
        ArrayList<Flow> flows = new ArrayList<>();
        flows.add(createFlow("brokertec-trade", "brokertec-trade", 1, 2));
        flows.add(createFlow("espeed-trade", "espeed-trade", 2, 2));
        flows.add(createFlow("tradeweb-trade", "tradeweb-trade", 2, 3));
        flows.add(createFlow("ion-trade", "ion-trade", 2, 4));
        flows.add(createFlow("blbgToms-mhiTrade", "blbgToms-mhiTrade", 4, 5));
        flows.add(createFlow("fios-trade", "fios-trade", 5, 7));
        flows.add(createFlow("murex-bondTrade", "murex-bondTrade", 5, 5));
        flows.add(createFlow("gloss-trade", "gloss-trade", 7, 3));

        ArrayList<IntegratedSystem> integratedSystems = new ArrayList<>();

        integratedSystems.add(createIntegratedSystem("Brokertec", "Brokertec", 1, 2));
        integratedSystems.add(createIntegratedSystem("Espeed", "Espeed", 1, 2));
        integratedSystems.add(createIntegratedSystem("Tradeweb", "TradeWeb", 1, 2));
        integratedSystems.add(createIntegratedSystem("ION", "ION", 1, 2));
        integratedSystems.add(createIntegratedSystem("Bloomberg", "Bloomberg TOMS", 3, 2));
        integratedSystems.add(createIntegratedSystem("Murex", "Murex", 6, 2));
        integratedSystems.add(createIntegratedSystem("City Fios", "City Fios", 6, 2));
        integratedSystems.add(createIntegratedSystem("Gloss", "Gloss", 8, 2));

        ArrayList<Edge> edges = new ArrayList<>();
        edges.add(createEdge("Brokertec", "brokertec-trade"));
        edges.add(createEdge("Tradeweb", "tradeweb-trade"));
        edges.add(createEdge("Espeed", "espeed-trade"));
        edges.add(createEdge("ION", "ion-trade"));
        edges.add(createEdge("brokertec-trade", "Bloomberg"));
        edges.add(createEdge("espeed-trade", "Bloomberg"));
        edges.add(createEdge("tradeweb-trade", "Bloomberg"));
        edges.add(createEdge("ion-trade", "Bloomberg"));
        edges.add(createEdge("Bloomberg", "blbgToms-mhiTrade"));
        edges.add(createEdge("blbgToms-mhiTrade", "murex-bondTrade"));
        edges.add(createEdge("murex-bondTrade", "Murex"));
        edges.add(createEdge("blbgToms-mhiTrade", "fios-trade"));
        edges.add(createEdge("fios-trade", "City Fios"));
        edges.add(createEdge("Murex", "gloss-trade"));
        edges.add(createEdge("gloss-trade", "Gloss"));

        Graph graph = new Graph();
        graph.setEdges(edges);
        graph.setIntegratedSystems(integratedSystems);
        graph.setFlows(flows);

        BusinessStreamVisjsAdapter adapter = new BusinessStreamVisjsAdapter();

        BusinessStream graph1 = adapter.toBusinessStreamGraph(adapter.toJson(graph));


    }

    private Flow createFlow(String id, String name, Integer x, Integer y)
    {
        Flow flow = new Flow();
        flow.setId(id);
        flow.setModuleName("moduleName");
        flow.setFlowName(name);
        flow.setX(x);
        flow.setY(y);

        Correlator correlator = new Correlator();
        correlator.setType("xpath");
        correlator.setQuery("value");

        flow.setCorrelator(correlator);

        return flow;
    }

    private IntegratedSystem createIntegratedSystem(String id, String name, Integer x, Integer y)
    {
        IntegratedSystem integrated = new IntegratedSystem();
        integrated.setId(id);
        integrated.setName(name);
        integrated.setX(x);
        integrated.setY(y);

        return integrated;
    }

    private Edge createEdge(String from, String to)
    {
        Edge edge = new Edge();
        edge.setFrom(from);
        edge.setTo(to);

        return edge;
    }
}
