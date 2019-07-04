package org.ikasan.vaadin.visjs.network.util;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = FontStyle.Builder.class)
public class FontStyle {

  private String color;
  private Integer size;
  private String face;
  private Integer vadjust;
  private Style mod;

  private FontStyle(Builder builder) {
    this.color = builder.color;
    this.size = builder.size;
    this.face = builder.face;
    this.vadjust = builder.vadjust;
    this.mod = builder.mod;
  }

  FontStyle() {}

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getFace() {
    return face;
  }

  public void setFace(String face) {
    this.face = face;
  }

  public int getVadjust() {
    return vadjust;
  }

  public void setVadjust(int vadjust) {
    this.vadjust = vadjust;
  }

  public Style getMod() {
    return mod;
  }

  public void setMod(Style mod) {
    this.mod = mod;
  }

  public static enum Style {
    bold, italic, bolditalic, mono;
  }

  /**
   * Creates builder to build {@link FontStyle}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link FontStyle}.
   */
  public static final class Builder {
    private String color;
    private Integer size;
    private String face;
    private Integer vadjust;
    private Style mod;

    private Builder() {}

    @Nonnull
    public Builder withColor(String color) {
      this.color = color;
      return this;
    }

    @Nonnull
    public Builder withSize(Integer size) {
      this.size = size;
      return this;
    }

    @Nonnull
    public Builder withFace(String face) {
      this.face = face;
      return this;
    }

    @Nonnull
    public Builder withVadjust(Integer vadjust) {
      this.vadjust = vadjust;
      return this;
    }

    @Nonnull
    public Builder withMod(Style mod) {
      this.mod = mod;
      return this;
    }

    @Nonnull
    public FontStyle build() {
      return new FontStyle(this);
    }
  }

}
