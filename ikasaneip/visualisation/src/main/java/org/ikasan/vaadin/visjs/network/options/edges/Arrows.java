package org.ikasan.vaadin.visjs.network.options.edges;

/**
 * Created by Martin Prause 5.8.2017
 */

public class Arrows {

  private ArrowHead to;
  private ArrowHead middle;
  private ArrowHead from;

  public Arrows() {

  }

  public Arrows(final ArrowHead to) {
    this.to = to;
  }

  public Arrows(final ArrowHead to, final ArrowHead middle) {
    this.to = to;
    this.middle = middle;
  }

  public Arrows(final ArrowHead to, final ArrowHead middle, final ArrowHead from) {
    this.to = to;
    this.middle = middle;
    this.from = from;
  }

  public ArrowHead getTo() {
    return to;
  }

  public void setTo(final ArrowHead to) {
    this.to = to;
  }

  public ArrowHead getMiddle() {
    return middle;
  }

  public void setMiddle(final ArrowHead middle) {
    this.middle = middle;
  }

  public ArrowHead getFrom() {
    return from;
  }

  public void setFrom(final ArrowHead from) {
    this.from = from;
  }

  public static enum Type {
    arrow, bar, circle;
  }

}
