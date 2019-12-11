package org.ikasan.vaadin.visjs.network.options.edges;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Smooth.Builder.class)
public class Smooth {

  private Boolean enabled;
  private Type type;
  @JsonIgnore
  private Boolean forceDirectionBoolean;
  @JsonIgnore
  private String forceDirectionStr;
  private Double roundness;

  private Smooth(Builder builder) {
    this.enabled = builder.enabled;
    this.type = builder.type;
    this.forceDirectionBoolean = builder.forceDirectionBoolean;
    this.forceDirectionStr = builder.forceDirectionStr;
    this.roundness = builder.roundness;
  }

  public Smooth() {}

  public Boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Double getRoundness() {
    return roundness;
  }

  public void setRoundness(Double roundness) {
    this.roundness = roundness;
  }

  @JsonGetter
  public Object getForceDirection() {
    return ObjectUtils.firstNonNull(forceDirectionBoolean, forceDirectionStr);
  }

  public Boolean getForceDirectionBoolean() {
    return forceDirectionBoolean;
  }

  public String getForceDirectionStr() {
    return forceDirectionStr;
  }

  public void setForceDirectionBoolean(Boolean forceDirection) {
    this.forceDirectionBoolean = forceDirection;
    if (this.forceDirectionBoolean != null) {
      this.forceDirectionStr = null;
    }
  }

  public void setForceDirectionStr(String forceDirectionStr) {
    this.forceDirectionStr = forceDirectionStr;
    if (this.forceDirectionStr != null) {
      this.forceDirectionBoolean = null;
    }
  }

  public static enum Type {
    dynamic, continuous, diagonalCross, straightCross, horizontal, vertical, curvedCW, curvedCCW, cubicBezier, discrete,
  }

  /**
   * Creates builder to build {@link Smooth}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Smooth}.
   */
  public static final class Builder {
    private Boolean enabled;
    private Type type;
    private Boolean forceDirectionBoolean;
    private String forceDirectionStr;
    private Double roundness;

    private Builder() {}

    @Nonnull
    public Builder withEnabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    @Nonnull
    public Builder withType(Type type) {
      this.type = type;
      return this;
    }

    @Nonnull
    public Builder withForceDirection(Boolean forceDirectionBoolean) {
      this.forceDirectionBoolean = forceDirectionBoolean;
      return this;
    }

    @Nonnull
    public Builder withForceDirection(String forceDirectionStr) {
      this.forceDirectionStr = forceDirectionStr;
      return this;
    }

    @Nonnull
    public Builder withRoundness(Double roundness) {
      this.roundness = roundness;
      return this;
    }

    @Nonnull
    public Smooth build() {
      return new Smooth(this);
    }
  }

}
