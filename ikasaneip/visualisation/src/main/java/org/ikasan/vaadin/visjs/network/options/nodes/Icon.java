package org.ikasan.vaadin.visjs.network.options.nodes;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Icon.Builder.class)
public class Icon {
  private Face face;
  private String code;
  private Integer size;
  private String color;

  private Icon(Builder builder) {
    this.face = builder.face;
    this.code = builder.code;
    this.size = builder.size;
    this.color = builder.color;
  }

  public Icon() {}

  public Face getFace() {
    return face;
  }

  public void setFace(Face face) {
    this.face = face;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public enum Face {
    FontAwesome, Ionicons
  }

  /**
   * Creates builder to build {@link Icon}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Icon}.
   */
  public static final class Builder {
    private Face face;
    private String code;
    private Integer size;
    private String color;

    private Builder() {}

    @Nonnull
    public Builder withFace(Face face) {
      this.face = face;
      return this;
    }

    @Nonnull
    public Builder withCode(String code) {
      this.code = code;
      return this;
    }

    @Nonnull
    public Builder withSize(Integer size) {
      this.size = size;
      return this;
    }

    @Nonnull
    public Builder withColor(String color) {
      this.color = color;
      return this;
    }

    @Nonnull
    public Icon build() {
      return new Icon(this);
    }
  }

}
