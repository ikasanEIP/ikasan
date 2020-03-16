package org.ikasan.vaadin.visjs.network;

import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;

public class Node extends Nodes
{

    private String id;
    private String edgeColour = "rgba(0, 255, 0, 0.8)";
    private String fillColour = "rgba(0, 255, 0, 0.2)";
    private String wiretapFoundStatus = NodeFoundStatus.EMPTY;
    private String wiretapFoundImage = null;
    private String errorFoundStatus = NodeFoundStatus.EMPTY;
    private String errorFoundImage = null;
    private String exclusionFoundStatus = NodeFoundStatus.EMPTY;
    private String exclusionFoundImage = null;
    private String replayFoundStatus = NodeFoundStatus.EMPTY;
    private String replayFoundImage = null;

    // @JsonIgnore
    // private transient List<Edge> edgeList = new ArrayList<>();;
    // @JsonIgnore
    // private transient Map<String, Edge> edgeMap = new HashMap<>();;

    public Node()
    {
    }

    public Node(final String id)
    {
        this.id = id;
    }

    public Node(final String id, Builder builder)
    {
        super(builder);
        this.id = id;
    }

    public Node(String id, String label)
    {
        this.id = id;
        this.setLabel(label);
    }

    public Node(String id, String label, final Builder builder)
    {
        super(builder);
        this.id = id;
        this.setLabel(label);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEdgeColour()
    {
        return edgeColour;
    }

    public void setEdgeColour(String edgeColour)
    {
        this.edgeColour = edgeColour;
    }

    public String getFillColour()
    {
        return fillColour;
    }

    public void setFillColour(String fillColour)
    {
        this.fillColour = fillColour;
    }

    public String getWiretapFoundStatus()
    {
        return wiretapFoundStatus;
    }

    public void setWiretapFoundStatus(String wiretapFoundStatus)
    {
        this.wiretapFoundStatus = wiretapFoundStatus;
    }

    public String getWiretapFoundImage()
    {
        return wiretapFoundImage;
    }

    public void setWiretapFoundImage(String wiretapFoundImage)
    {
        this.wiretapFoundImage = wiretapFoundImage;
    }

    public String getErrorFoundStatus() {
        return errorFoundStatus;
    }

    public void setErrorFoundStatus(String errorFoundStatus) {
        this.errorFoundStatus = errorFoundStatus;
    }

    public String getErrorFoundImage() {
        return errorFoundImage;
    }

    public void setErrorFoundImage(String errorFoundImage) {
        this.errorFoundImage = errorFoundImage;
    }

    public String getExclusionFoundStatus() {
        return exclusionFoundStatus;
    }

    public void setExclusionFoundStatus(String exclusionFoundStatus) {
        this.exclusionFoundStatus = exclusionFoundStatus;
    }

    public String getExclusionFoundImage() {
        return exclusionFoundImage;
    }

    public void setExclusionFoundImage(String exclusionFoundImage) {
        this.exclusionFoundImage = exclusionFoundImage;
    }

    public String getReplayFoundStatus() {
        return replayFoundStatus;
    }

    public void setReplayFoundStatus(String replayFoundStatus) {
        this.replayFoundStatus = replayFoundStatus;
    }

    public String getReplayFoundImage() {
        return replayFoundImage;
    }

    public void setReplayFoundImage(String replayFoundImage) {
        this.replayFoundImage = replayFoundImage;
    }

    // FIXME sync with JS-Component
    // public List<Edge> getConnectedEdges() {
    // return edgeList;
    // }
    //
    // public void setConnectedEdges(List<Edge> edgeList) {
    // this.edgeList = edgeList;
    // }
    //
    // public void addEdgeToList(Edge edge) {
    // edgeList.add(edge);
    // }
    //
    // public void removeEdgeFromList(Edge edge) {
    // edgeList.remove(edge);
    // }
    //
    // public Map<String, Edge> getEdgeMap() {
    // return edgeMap;
    // }
    //
    // public void setEdgeMap(Map<String, Edge> edgeMap) {
    // this.edgeMap = edgeMap;
    // }

}
