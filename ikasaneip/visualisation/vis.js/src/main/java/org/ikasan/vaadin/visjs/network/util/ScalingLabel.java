package org.ikasan.vaadin.visjs.network.util;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = ScalingLabel.Builder.class)
public class ScalingLabel {

  private Boolean enabled;
  private Integer min;
  private Integer max;
  private Integer maxVisible;
  private Integer drawThreshold;

  private ScalingLabel(Builder builder) {
    this.enabled = builder.enabled;
    this.min = builder.min;
    this.max = builder.max;
    this.maxVisible = builder.maxVisible;
    this.drawThreshold = builder.drawThreshold;
  }

  public ScalingLabel() {}

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

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

  public Integer getMaxVisible() {
    return maxVisible;
  }

  public void setMaxVisible(Integer maxVisible) {
    this.maxVisible = maxVisible;
  }

  public Integer getDrawThreshold() {
    return drawThreshold;
  }

  public void setDrawThreshold(Integer drawThreshold) {
    this.drawThreshold = drawThreshold;
  }

  /**
   * Creates builder to build {@link ScalingLabel}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link ScalingLabel}.
   */
  public static final class Builder {
    private Boolean enabled;
    private Integer min;
    private Integer max;
    private Integer maxVisible;
    private Integer drawThreshold;

    private Builder() {}

    @Nonnull
    public Builder withEnabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }

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
    public Builder withMaxVisible(Integer maxVisible) {
      this.maxVisible = maxVisible;
      return this;
    }

    @Nonnull
    public Builder withDrawThreshold(Integer drawThreshold) {
      this.drawThreshold = drawThreshold;
      return this;
    }

    @Nonnull
    public ScalingLabel build() {
      return new ScalingLabel(this);
    }
  }

}
