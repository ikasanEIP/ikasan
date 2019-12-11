package org.ikasan.vaadin.visjs.network.api;

/**
 * Created by Martin Prause 9.8.2017
 */

public class Pointer {

  Coordinates DOM;
  Coordinates canvas;

  public Pointer() {
    DOM = new Coordinates();
    canvas = new Coordinates();
  }

  public Coordinates getDOM() {
    return DOM;
  }

  public void setDOM(Coordinates dOM) {
    DOM = dOM;
  }

  public Coordinates getCanvas() {
    return canvas;
  }

  public void setCanvas(Coordinates canvas) {
    this.canvas = canvas;
  }

}
