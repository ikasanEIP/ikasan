package org.ikasan.vaadin.visjs.network.options.physics;

public class Stabilization {
  private boolean enabled = true;
  private int iterations = 1000;
  private int updateInterval = 100;
  private boolean onlyDynamicEdges = false;
  private boolean fit = true;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public int getIterations() {
    return iterations;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public int getUpdateInterval() {
    return updateInterval;
  }

  public void setUpdateInterval(int updateInterval) {
    this.updateInterval = updateInterval;
  }

  public boolean isOnlyDynamicEdges() {
    return onlyDynamicEdges;
  }

  public void setOnlyDynamicEdges(boolean onlyDynamicEdges) {
    this.onlyDynamicEdges = onlyDynamicEdges;
  }

  public boolean isFit() {
    return fit;
  }

  public void setFit(boolean fit) {
    this.fit = fit;
  }

}
