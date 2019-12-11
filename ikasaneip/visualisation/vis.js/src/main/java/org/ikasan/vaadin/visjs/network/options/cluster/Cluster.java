package org.ikasan.vaadin.visjs.network.options.cluster;

/**
 * Created by roshans on 10/30/14.
 */
public class Cluster {

  private int initialMaxNodes = 100;
  private int clusterThreshold = 500;
  private int reduceToNodes = 300;
  private int clusterEdgeThreshold = 20;
  private int sectorThreshold = 50;
  private int maxFontSize = 1000;
  private int edgeGrowth = 20;
  private int maxNodeSizeIncrements = 600;
  private int activeAreaBoxSize = 100;
  private int clusterLevelDifference = 2;

  private float chainThreshold = 0.4f;
  private float screenSizeThreshold = 0.2f;
  private float fontSizeMultiplier = 4.0f;
  private float forceAmplification = 0.6f;
  private float distanceAmplification = 0.2f;

  private NodeScale nodeScaling = new NodeScale();

  public int getInitialMaxNodes() {
    return initialMaxNodes;
  }

  public void setInitialMaxNodes(int initialMaxNodes) {
    this.initialMaxNodes = initialMaxNodes;
  }

  public int getClusterThreshold() {
    return clusterThreshold;
  }

  public void setClusterThreshold(int clusterThreshold) {
    this.clusterThreshold = clusterThreshold;
  }

  public int getReduceToNodes() {
    return reduceToNodes;
  }

  public void setReduceToNodes(int reduceToNodes) {
    this.reduceToNodes = reduceToNodes;
  }

  public int getClusterEdgeThreshold() {
    return clusterEdgeThreshold;
  }

  public void setClusterEdgeThreshold(int clusterEdgeThreshold) {
    this.clusterEdgeThreshold = clusterEdgeThreshold;
  }

  public int getSectorThreshold() {
    return sectorThreshold;
  }

  public void setSectorThreshold(int sectorThreshold) {
    this.sectorThreshold = sectorThreshold;
  }

  public int getMaxFontSize() {
    return maxFontSize;
  }

  public void setMaxFontSize(int maxFontSize) {
    this.maxFontSize = maxFontSize;
  }

  public int getEdgeGrowth() {
    return edgeGrowth;
  }

  public void setEdgeGrowth(int edgeGrowth) {
    this.edgeGrowth = edgeGrowth;
  }

  public int getMaxNodeSizeIncrements() {
    return maxNodeSizeIncrements;
  }

  public void setMaxNodeSizeIncrements(int maxNodeSizeIncrements) {
    this.maxNodeSizeIncrements = maxNodeSizeIncrements;
  }

  public int getActiveAreaBoxSize() {
    return activeAreaBoxSize;
  }

  public void setActiveAreaBoxSize(int activeAreaBoxSize) {
    this.activeAreaBoxSize = activeAreaBoxSize;
  }

  public int getClusterLevelDifference() {
    return clusterLevelDifference;
  }

  public void setClusterLevelDifference(int clusterLevelDifference) {
    this.clusterLevelDifference = clusterLevelDifference;
  }

  public float getChainThreshold() {
    return chainThreshold;
  }

  public void setChainThreshold(float chainThreshold) {
    this.chainThreshold = chainThreshold;
  }

  public float getScreenSizeThreshold() {
    return screenSizeThreshold;
  }

  public void setScreenSizeThreshold(float screenSizeThreshold) {
    this.screenSizeThreshold = screenSizeThreshold;
  }

  public float getFontSizeMultiplier() {
    return fontSizeMultiplier;
  }

  public void setFontSizeMultiplier(float fontSizeMultiplier) {
    this.fontSizeMultiplier = fontSizeMultiplier;
  }

  public float getForceAmplification() {
    return forceAmplification;
  }

  public void setForceAmplification(float forceAmplification) {
    this.forceAmplification = forceAmplification;
  }

  public float getDistanceAmplification() {
    return distanceAmplification;
  }

  public void setDistanceAmplification(float distanceAmplification) {
    this.distanceAmplification = distanceAmplification;
  }

  public NodeScale getNodeScaling() {
    return nodeScaling;
  }

  public void setNodeScaling(NodeScale nodeScaling) {
    this.nodeScaling = nodeScaling;
  }
}
