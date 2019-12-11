package org.ikasan.vaadin.visjs.network.options.physics;

/**
 * Created by roshans on 10/29/14.
 */
public class Physics {

  private boolean enabled = true;
  private BarnesHut barnesHut;
  private ForceAtlas2Based forceAtlas2Based;
  private Repulsion repulsion;
  private HierarchicalRepulsion hierarchicalRepulsion;

  int maxVelocity = 50;
  double minVelocity = 0.1f;
  Solver solver = Solver.barnesHut;

  double timestep = 0.5;
  boolean adaptiveTimestep = true;
  Stabilization stabilization;

  public BarnesHut getBarnesHut() {
    return barnesHut;
  }

  public void setBarnesHut(BarnesHut barnesHut) {
    this.barnesHut = barnesHut;
  }

  public Repulsion getRepulsion() {
    return repulsion;
  }

  public void setRepulsion(Repulsion repulsion) {
    this.repulsion = repulsion;
  }

  public HierarchicalRepulsion getHierarchicalRepulsion() {
    return hierarchicalRepulsion;
  }

  public void setHierarchicalRepulsion(HierarchicalRepulsion hierarchicalRepulsion) {
    this.hierarchicalRepulsion = hierarchicalRepulsion;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public ForceAtlas2Based getForceAtlas2Based() {
    return forceAtlas2Based;
  }

  public void setForceAtlas2Based(ForceAtlas2Based forceAtlas2Based) {
    this.forceAtlas2Based = forceAtlas2Based;
  }

  public int getMaxVelocity() {
    return maxVelocity;
  }

  public void setMaxVelocity(int maxVelocity) {
    this.maxVelocity = maxVelocity;
  }

  public double getMinVelocity() {
    return minVelocity;
  }

  public void setMinVelocity(double minVelocity) {
    this.minVelocity = minVelocity;
  }

  public Solver getSolver() {
    return solver;
  }

  public void setSolver(Solver solver) {
    this.solver = solver;
  }

  public double getTimestep() {
    return timestep;
  }

  public void setTimestep(double timestep) {
    this.timestep = timestep;
  }

  public boolean isAdaptiveTimestep() {
    return adaptiveTimestep;
  }

  public void setAdaptiveTimestep(boolean adaptiveTimestep) {
    this.adaptiveTimestep = adaptiveTimestep;
  }

  public Stabilization getStabilization() {
    return stabilization;
  }

  public void setStabilization(Stabilization stabilization) {
    this.stabilization = stabilization;
  }

  public static enum Solver {
    barnesHut, repulsion, hierarchicalRepulsion, forceAtlas2Based;
  }

}
