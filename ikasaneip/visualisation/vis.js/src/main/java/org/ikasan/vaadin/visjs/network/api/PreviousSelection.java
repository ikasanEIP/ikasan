package org.ikasan.vaadin.visjs.network.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Martin Prause 9.8.2017
 */

public class PreviousSelection {
  private List<String> nodeIds;
  private List<String> edgeIds;

  public PreviousSelection() {

    nodeIds = new ArrayList<>();
    edgeIds = new ArrayList<>();

  }

  public List<String> getNodeIds() {
    return nodeIds;
  }

  public void setNodeIds(List<String> nodeIds) {
    this.nodeIds = nodeIds;
  }

  public List<String> getEdgeIds() {
    return edgeIds;
  }

  public void setEdgeIds(List<String> edgeIds) {
    this.edgeIds = edgeIds;
  }

}
