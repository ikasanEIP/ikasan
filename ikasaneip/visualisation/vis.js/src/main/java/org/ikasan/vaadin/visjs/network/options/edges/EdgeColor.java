package org.ikasan.vaadin.visjs.network.options.edges;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = EdgeColor.Builder.class)
public class EdgeColor {

  private String color;
  private String highlight;
  private String hover;
  private String inherit;
  private Integer opacity;

  private EdgeColor(Builder builder) {
    this.color = builder.color;
    this.highlight = builder.highlight;
    this.hover = builder.hover;
    this.inherit = builder.inherit;
    this.opacity = builder.opacity;
  }

  public EdgeColor() {}

  public Integer getOpacity() {
    return opacity;
  }

  public void setOpacity(final Integer opacity) {
    this.opacity = opacity;
  }

  public String getHover() {
    return hover;
  }

  public void setHover(final String hover) {
    this.hover = hover;
  }

  public String getHighlight() {
    return highlight;
  }

  public void setHighlight(final String highlight) {
    this.highlight = highlight;
  }

  public String getColor() {
    return color;
  }

  public void setColor(final String color) {
    this.color = color;
  }

  public String getInherit() {
    return inherit;
  }

  public void setInherit(String inherit) {
    this.inherit = inherit;
  }

  /**
   * Creates builder to build {@link EdgeColor}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link EdgeColor}.
   */
  public static final class Builder {
    private String color;
    private String highlight;
    private String hover;
    private String inherit;
    private Integer opacity;

    private Builder() {}

    @Nonnull
    public Builder withColor(String color) {
      this.color = color;
      return this;
    }

    @Nonnull
    public Builder withHighlight(String highlight) {
      this.highlight = highlight;
      return this;
    }

    @Nonnull
    public Builder withHover(String hover) {
      this.hover = hover;
      return this;
    }

    @Nonnull
    public Builder withInherit(String inherit) {
      this.inherit = inherit;
      return this;
    }

    @Nonnull
    public Builder withOpacity(Integer opacity) {
      this.opacity = opacity;
      return this;
    }

    @Nonnull
    public EdgeColor build() {
      return new EdgeColor(this);
    }
  }
}
