package org.ikasan.vaadin.visjs.network.options.nodes;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.ikasan.vaadin.visjs.network.util.SimpleColor;

@JsonDeserialize(builder = NodeColor.Builder.class)
public class NodeColor {

  @JsonIgnore
  private String hoverStr;
  @JsonIgnore
  private SimpleColor hoverColor;
  @JsonIgnore
  private String highlightStr;
  @JsonIgnore
  private SimpleColor highlightColor;
  private String border;
  private String background;
  private Integer opacity;

  private NodeColor(Builder builder) {
    this.hoverStr = builder.hoverStr;
    this.hoverColor = builder.hoverColor;
    this.highlightStr = builder.highlightStr;
    this.highlightColor = builder.highlightColor;
    this.border = builder.border;
    this.background = builder.background;
    this.opacity = builder.opacity;
  }

  public NodeColor() {}

  public Integer getOpacity() {
    return opacity;
  }

  public void setOpacity(final Integer opacity) {
    this.opacity = opacity;
  }

  @JsonGetter(value = "hover")
  public Object getHover() {
    return ObjectUtils.firstNonNull(hoverColor, hoverStr);
  }

  public SimpleColor getHoverColor() {
    return hoverColor;
  }

  public String getHoverStr() {
    return hoverStr;
  }

  public void setHover(final SimpleColor hoverColor) {
    this.hoverColor = hoverColor;
    this.hoverStr = null;
  }

  public void setHover(String hoverStr) {
    this.hoverStr = hoverStr;
    this.hoverColor = null;
  }

  @JsonGetter(value = "highlight")
  public Object getHighlight() {
    return ObjectUtils.firstNonNull(highlightColor, highlightStr);
  }

  public void setHighlight(final SimpleColor highlightColor) {
    this.highlightColor = highlightColor;
    this.highlightStr = null;
  }

  public void setHighlight(String highlightStr) {
    this.highlightStr = highlightStr;
    this.highlightColor = null;
  }

  public String getBorder() {
    return border;
  }

  public void setBorder(final String border) {
    this.border = border;
  }

  public String getBackground() {
    return background;
  }

  public void setBackground(final String background) {
    this.background = background;
  }

  /**
   * Creates builder to build {@link NodeColor}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link NodeColor}.
   */
  public static final class Builder {
    private String hoverStr;
    private SimpleColor hoverColor;
    private String highlightStr;
    private SimpleColor highlightColor;
    private String border;
    private String background;
    private Integer opacity;

    private Builder() {}

    @Nonnull
    public Builder withHoverStr(String hoverStr) {
      this.hoverStr = hoverStr;
      return this;
    }

    @Nonnull
    public Builder withHoverColor(SimpleColor hoverColor) {
      this.hoverColor = hoverColor;
      return this;
    }

    @Nonnull
    public Builder withHighlightStr(String highlightStr) {
      this.highlightStr = highlightStr;
      return this;
    }

    @Nonnull
    public Builder withHighlightColor(SimpleColor highlightColor) {
      this.highlightColor = highlightColor;
      return this;
    }

    @Nonnull
    public Builder withBorder(String border) {
      this.border = border;
      return this;
    }

    @Nonnull
    public Builder withBackground(String background) {
      this.background = background;
      return this;
    }

    @Nonnull
    public Builder withOpacity(Integer opacity) {
      this.opacity = opacity;
      return this;
    }

    @Nonnull
    public NodeColor build() {
      return new NodeColor(this);
    }
  }

}
