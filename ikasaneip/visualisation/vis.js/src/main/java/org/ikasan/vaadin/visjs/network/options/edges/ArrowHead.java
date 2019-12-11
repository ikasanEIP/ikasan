package org.ikasan.vaadin.visjs.network.options.edges;

public class ArrowHead {
  private boolean enabled = false;
  private int scaleFactor = 1;
  private Arrows.Type type = Arrows.Type.arrow;

  public ArrowHead() {
    enabled = true;
  }

  public ArrowHead(final int scaleFactor, final Arrows.Type type) {
    enabled = true;
    this.scaleFactor = scaleFactor;
    this.type = type;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }

  public int getScaleFactor() {
    return scaleFactor;
  }

  public void setScaleFactor(final int scaleFactor) {
    this.scaleFactor = scaleFactor;
  }

  public Arrows.Type getType() {
    return type;
  }

  public void setType(final Arrows.Type type) {
    this.type = type;
  }

}
