package org.ikasan.vaadin.visjs.network;

import org.ikasan.vaadin.visjs.network.options.edges.EdgeColor;
import org.ikasan.vaadin.visjs.network.options.edges.Edges;

/**
 */
public class Edge extends Edges {

  private String from;
  private String to;
  private String id;

  public Edge(final Node from, final Node to) {
    this.from = from.getId();
    this.to = to.getId();
  }

  public Edge(final int from, final int to) {
    this.from = Integer.toString(from);
    this.to = Integer.toString(to);
  }

  public Edge(final String from, final String to) {
    this.from = from;
    this.to = to;
  }

  public Edge(final int from, final int to, final int width) {
    this.from = Integer.toString(from);
    this.to = Integer.toString(to);
    setWidth(width);
  }

  public Edge(final String from, final String to, final int width) {
    this.from = from;
    this.to = to;
    setWidth(width);
  }

  public Edge(final int from, final int to, final EdgeColor color) {
    this.from = Integer.toString(from);
    this.to = Integer.toString(to);
    setColor(color);
  }

  public Edge(final String from, final String to, final EdgeColor color) {
    this.from = from;
    this.to = to;
    setColor(color);
  }

  public Edge(final int from, final int to, final EdgeColor color, final int width) {
    this.from = Integer.toString(from);
    this.to = Integer.toString(to);
    setColor(color);
    setWidth(width);
  }

  public Edge(final String from, final String to, final EdgeColor color, final int width) {
    this.from = from;
    this.to = to;
    setColor(color);
    setWidth(width);
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public String getFrom() {
    return from;
  }

  public void setFrom(final String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(final String to) {
    this.to = to;
  }

}
