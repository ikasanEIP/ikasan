package org.ikasan.vaadin.visjs.network.util;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Scaling.Builder.class)
public class Scaling {

  Integer min;
  Integer max;
  ScalingLabel label;

  private Scaling(Builder builder) {
    this.min = builder.min;
    this.max = builder.max;
    this.label = builder.label;
  }

  public Scaling() {}

  public Integer getMin() {
    return min;
  }

  public void setMin(Integer min) {
    this.min = min;
  }

  public Integer getMax() {
    return max;
  }

  public void setMax(Integer max) {
    this.max = max;
  }

  public ScalingLabel getLabel() {
    return label;
  }

  public void setLabel(ScalingLabel label) {
    this.label = label;
  }

  /**
   * Creates builder to build {@link Scaling}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Scaling}.
   */
  public static final class Builder {
    private Integer min;
    private Integer max;
    private ScalingLabel label;

    private Builder() {}

    @Nonnull
    public Builder withMin(Integer min) {
      this.min = min;
      return this;
    }

    @Nonnull
    public Builder withMax(Integer max) {
      this.max = max;
      return this;
    }

    @Nonnull
    public Builder withLabel(ScalingLabel label) {
      this.label = label;
      return this;
    }

    @Nonnull
    public Scaling build() {
      return new Scaling(this);
    }
  }

}
