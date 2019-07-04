package org.ikasan.vaadin.visjs.network.options.nodes;

/**
 * Created by Martin Prause 9.8.2017
 */

public class WidthConstraint {
  private int minimum;
  private int maximum;

  public WidthConstraint() {

  }

  public WidthConstraint(int minimum, int maximum) {
    this.minimum = minimum;
    this.maximum = maximum;

  }

  public int getMinimum() {
    return minimum;
  }

  public void setMinimum(int minimum) {
    this.minimum = minimum;
  }

  public int getMaximum() {
    return maximum;
  }

  public void setMaximum(int maximum) {
    this.maximum = maximum;
  }

}
