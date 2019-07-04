package org.ikasan.vaadin.visjs.network.util;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Shadow.Builder.class)
public class Shadow {
  private Boolean enabled;
  private String color;
  private Integer size;
  private Integer x;
  private Integer y;

  private Shadow(Builder builder) {
    this.enabled = builder.enabled;
    this.color = builder.color;
    this.size = builder.size;
    this.x = builder.x;
    this.y = builder.y;
  }

  public Shadow() {}

  public Boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public Integer getSize() {
    return size;
  }

  public void setSize(Integer size) {
    this.size = size;
  }

  public Integer getX() {
    return x;
  }

  public void setX(Integer x) {
    this.x = x;
  }

  public Integer getY() {
    return y;
  }

  public void setY(Integer y) {
    this.y = y;
  }

  /**
   * Creates builder to build {@link Shadow}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Shadow}.
   */
  public static final class Builder {
    private Boolean enabled;
    private String color;
    private Integer size;
    private Integer x;
    private Integer y;

    private Builder() {}

    @Nonnull
    public Builder withEnabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }

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
    public Builder withx(Integer x) {
      this.x = x;
      return this;
    }

    @Nonnull
    public Builder withy(Integer y) {
      this.y = y;
      return this;
    }

    @Nonnull
    public Shadow build() {
      return new Shadow(this);
    }
  }

}
