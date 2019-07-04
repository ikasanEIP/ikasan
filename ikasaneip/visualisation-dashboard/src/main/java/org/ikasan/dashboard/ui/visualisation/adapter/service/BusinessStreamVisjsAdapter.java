package org.ikasan.dashboard.ui.visualisation.adapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.dashboard.ui.visualisation.correlate.XpathCorrelator;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Destination;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.dashboard.ui.visualisation.adapter.model.Graph;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStreamGraph;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.IntegratedSystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BusinessStreamVisjsAdapter
{
    /**
     * Converts a Graph object model to a JSON String.
     *
     * @param graph
     * @return
     * @throws JsonProcessingException
     */
    public String toJson(Graph graph) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(graph);
    }

    /**
     * Converts a JSON representation of a BusinessStreamGraph to a BusinessStreamGraph
     * object model.
     *
     * @param businessStreamGraph a JSON representation of a BusinessStreamGraph
     * @return an initialised BusinessStreamGraph
     * @throws IOException
     */
    public BusinessStreamGraph toBusinessStreamGraph(String businessStreamGraph) throws IOException
    {
        Graph graph = this.toGraph(businessStreamGraph);

        return new BusinessStreamGraph(getFlows(graph.getFlows()),
            getIntegratedSystem(graph.getIntegratedSystems()),
            getEdges(graph.getEdges()), getDestinations(graph.getDestinations()));
    }

    /**
     * Helper method to convert a graph JSON String to a Graph.
     *
     * @param graph the graph JSON String.
     * @return and initialised Graph
     * @throws IOException
     */
    private Graph toGraph(String graph) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(graph, Graph.class);
    }

    /**
     * Helper method to convert raw flows to decorated flows.
     *
     * @param flows the raw flows
     * @return decorated flows
     */
    private List<Flow> getFlows(List<org.ikasan.dashboard.ui.visualisation.adapter.model.Flow> flows)
    {
        ArrayList<Flow> flows1 = new ArrayList<>();

        for(org.ikasan.dashboard.ui.visualisation.adapter.model.Flow flow: flows)
       {
           flows1.add(getFlow(flow));
       }

       return flows1;
    }

    /**
     * Helper method to convert raw flow to a decorated flow.
     *
     * @param flow the raw flow
     * @return decorated flow
     */
    private Flow getFlow(org.ikasan.dashboard.ui.visualisation.adapter.model.Flow flow)
    {
        Flow f = new Flow(flow.getId(), flow.getName(), flow.getX(), flow.getY());
        if(flow.getCorrelator() != null)
        {
            if(flow.getCorrelator().getType().equals("xpath"))
            {
                XpathCorrelator xpathCorrelator
                    = new XpathCorrelator(flow.getCorrelator().getQuery());
                f.setCorrelator(xpathCorrelator);
            }
        }
        return f;
    }

    /**
     * Helper method to convert raw IntegratedSystems to decorated IntegratedSystems.
     *
     * @param integratedSystems
     * @return
     */
    private List<IntegratedSystem> getIntegratedSystem(List<org.ikasan.dashboard.ui.visualisation.adapter.model.IntegratedSystem> integratedSystems)
    {
        ArrayList<IntegratedSystem> integratedSystems1 = new ArrayList<>();

        for(org.ikasan.dashboard.ui.visualisation.adapter.model.IntegratedSystem integratedSystem: integratedSystems)
        {
            integratedSystems1.add(getIntegratedSystem(integratedSystem));
        }

        return integratedSystems1;
    }

    /**
     * Helper method to convert raw IntegratedSystem to a decorated IntegratedSystem.
     *
     * @param integratedSystem
     * @return
     */
    private IntegratedSystem getIntegratedSystem(org.ikasan.dashboard.ui.visualisation.adapter.model.IntegratedSystem integratedSystem)
    {
        return new IntegratedSystem(integratedSystem.getId(), integratedSystem.getName(), integratedSystem.getX(), integratedSystem.getY());
    }

    /**
     * Helper method to convert raw Edges to decorated Edges.
     *
     * @param edges
     * @return
     */
    private List<Edge> getEdges(List<org.ikasan.dashboard.ui.visualisation.adapter.model.Edge> edges)
    {
        ArrayList<Edge> edges1 = new ArrayList<>();

        for(org.ikasan.dashboard.ui.visualisation.adapter.model.Edge edge: edges)
        {
            edges1.add(getEdge(edge));
        }

        return edges1;
    }

    /**
     * Helper method to convert a raw Edge to a decorated Edges.
     *
     * @param edge
     * @return
     */
    private Edge getEdge(org.ikasan.dashboard.ui.visualisation.adapter.model.Edge edge)
    {
        return new Edge(edge.getFrom(), edge.getTo());
    }

    /**
     * Helper method to convert raw Destinations to decorated Destinations.
     *
     * @param destinations
     * @return
     */
    private List<Destination> getDestinations(List<org.ikasan.dashboard.ui.visualisation.adapter.model.Destination> destinations)
    {
        ArrayList<Destination> destinations1 = new ArrayList<>();

        for(org.ikasan.dashboard.ui.visualisation.adapter.model.Destination destination: destinations)
        {
            destinations1.add(getDestination(destination));
        }

        return destinations1;
    }

    /**
     * Helper method to convert a raw Destination to a decorated Destinations.
     *
     * @param destination
     * @return
     */
    private Destination getDestination(org.ikasan.dashboard.ui.visualisation.adapter.model.Destination destination)
    {
        return new Destination(destination.getId(), destination.getName(), destination.getX(), destination.getY());
    }
}
