package org.ikasan.vaadin.visjs.network.options.edges;

import org.ikasan.vaadin.visjs.network.options.HierarchicalLayout;

/**
 * Created by Martin Prause 5.8.2017
 */

public class Layout {

  // randomSeed: undefined,

  boolean improvedLayout = true;
  HierarchicalLayout hierarchical;

  public boolean isImprovedLayout() {
    return improvedLayout;
  }

  public void setImprovedLayout(boolean improvedLayout) {
    this.improvedLayout = improvedLayout;
  }

  public HierarchicalLayout getHierarchical() {
    return hierarchical;
  }

  public void setHierarchical(HierarchicalLayout hierarchical) {
    this.hierarchical = hierarchical;
  }

}
