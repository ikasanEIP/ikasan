package org.ikasan.vaadin.visjs.network.util;

public class SimpleColor {

  private String border;
  private String background;

  public SimpleColor() {}

  /**
   *
   * @param background might be null
   * @param border might be null
   */
  public SimpleColor(String background, String border) {
    this.background = background;
    this.border = border;
  }

  public String getBorderColor() {
    return border;
  }

  public void setBorderColor(final String border) {
    this.border = border;
  }

  public String getBackgroundColor() {
    return background;
  }

  public void setBackgroundColor(final String background) {
    this.background = background;
  }
}
