package org.ikasan.dashboard.ui.visualisation.adapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.business.stream.metadata.model.BusinessStream;
import org.ikasan.dashboard.ui.visualisation.correlate.XpathCorrelator;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Boundary;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Destination;
import org.ikasan.spec.metadata.BusinessStreamMetaData;
import org.ikasan.vaadin.visjs.network.Edge;
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
     * @param businessStream
     * @return
     * @throws JsonProcessingException
     */
    public String toJson(BusinessStream businessStream) throws JsonProcessingException
    {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(businessStream);
    }

    /**
     * Converts a BusinessStreamMetaData representation of a BusinessStream to a BusinessStream
     * object model.
     *
     * @param businessStreamMetaData the BusinessStreamMetaData
     * @return an initialised BusinessStream
     * @throws IOException
     */
    public org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream toBusinessStreamGraph(BusinessStreamMetaData<BusinessStream> businessStreamMetaData) throws IOException
    {
        return new org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream(getFlows(businessStreamMetaData.getBusinessStream().getFlows()),
            getIntegratedSystem(businessStreamMetaData.getBusinessStream().getIntegratedSystems()),
            getEdges(businessStreamMetaData.getBusinessStream().getEdges()), getDestinations(businessStreamMetaData.getBusinessStream().getDestinations()),
            getBoundaries(businessStreamMetaData.getBusinessStream().getBoundaries()));
    }

    /**
     * Helper method to convert a graph JSON String to a Graph.
     *
     * @param graph the graph JSON String.
     * @return and initialised Graph
     * @throws IOException
     */
    private BusinessStream toGraph(String graph) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(graph, BusinessStream.class);
    }

    /**
     * Helper method to convert raw flows to decorated flows.
     *
     * @param flows the raw flows
     * @return decorated flows
     */
    private List<Flow> getFlows(List<org.ikasan.business.stream.metadata.model.Flow> flows)
    {
        ArrayList<Flow> flows1 = new ArrayList<>();

        for(org.ikasan.business.stream.metadata.model.Flow flow: flows)
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
    private Flow getFlow(org.ikasan.business.stream.metadata.model.Flow flow)
    {
        Flow f = new Flow(flow.getId(), flow.getModuleName(), flow.getFlowName(), flow.getX(), flow.getY());
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
    private List<IntegratedSystem> getIntegratedSystem(List<org.ikasan.business.stream.metadata.model.IntegratedSystem> integratedSystems)
    {
        ArrayList<IntegratedSystem> integratedSystems1 = new ArrayList<>();

        for(org.ikasan.business.stream.metadata.model.IntegratedSystem integratedSystem: integratedSystems)
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
    private IntegratedSystem getIntegratedSystem(org.ikasan.business.stream.metadata.model.IntegratedSystem integratedSystem)
    {
        return new IntegratedSystem(integratedSystem.getId(), integratedSystem.getName(), integratedSystem.getImage(),
            integratedSystem.getSize(), integratedSystem.getX(), integratedSystem.getY());
    }

    /**
     * Helper method to convert raw Edges to decorated Edges.
     *
     * @param edges
     * @return
     */
    private List<Edge> getEdges(List<org.ikasan.business.stream.metadata.model.Edge> edges)
    {
        ArrayList<Edge> edges1 = new ArrayList<>();

        for(org.ikasan.business.stream.metadata.model.Edge edge: edges)
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
    private Edge getEdge(org.ikasan.business.stream.metadata.model.Edge edge)
    {
        return new Edge(edge.getFrom(), edge.getTo());
    }

    /**
     * Helper method to convert raw Destinations to decorated Destinations.
     *
     * @param destinations
     * @return
     */
    private List<Destination> getDestinations(List<org.ikasan.business.stream.metadata.model.Destination> destinations)
    {
        ArrayList<Destination> destinations1 = new ArrayList<>();

        for(org.ikasan.business.stream.metadata.model.Destination destination: destinations)
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
    private Destination getDestination(org.ikasan.business.stream.metadata.model.Destination destination)
    {
        return new Destination(destination.getId(), destination.getName(), destination.getX(), destination.getY());
    }

    /**
     * Helper method to convert raw Boundaries to decorated Boundaries.
     *
     * @param boundaries
     * @return
     */
    private List<Boundary> getBoundaries(List<org.ikasan.business.stream.metadata.model.Boundary> boundaries)
    {
        ArrayList<Boundary> boundaries1 = new ArrayList<>();

        boundaries.forEach(boundary -> boundaries1.add(getBoundary(boundary)));

        return boundaries1;
    }

    /**
     * Helper method to convert a raw Boundary to a decorated Boundary.
     *
     * @param boundary
     * @return
     */
    private Boundary getBoundary(org.ikasan.business.stream.metadata.model.Boundary boundary)
    {
        return new Boundary(boundary.getX(), boundary.getY(), boundary.getW(), boundary.getH()
            , boundary.getColour(), boundary.getLabel());
    }
}
