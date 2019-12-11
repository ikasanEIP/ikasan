package org.ikasan.vaadin.visjs.network.options.edges;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.ikasan.vaadin.visjs.network.util.Font;
import org.ikasan.vaadin.visjs.network.util.Scaling;
import org.ikasan.vaadin.visjs.network.util.Shadow;

@JsonDeserialize(builder = Edges.Builder.class)
public class Edges {

  @JsonIgnore
  private Arrows arrowsObject;
  @JsonIgnore
  private String arrowsStr;
  private Boolean arrowStrikethrough;
  // can be object too
  private Boolean chosen;
  @JsonIgnore
  private EdgeColor colorObject;
  @JsonIgnore
  private String colorStr;
  @JsonIgnore
  private Integer dashesArray[];
  @JsonIgnore
  private Boolean dashesBoolean;
  @JsonIgnore
  private Font fontObject;
  @JsonIgnore
  private String fontStr;

  private Boolean hidden;
  // Can be function too
  private Double hoverWidth;
  private String label;
  private Boolean labelHighlightBold;
  private Integer length;
  private Boolean physics;

  private Scaling scaling;
  // Can be funtion too
  private Integer selectionWidth;
  private Integer selfReferenceSize;
  @JsonIgnore
  private Shadow shadowObject;
  @JsonIgnore
  private Boolean shadowBoolean;
  @JsonIgnore
  private Smooth smoothObject;
  @JsonIgnore
  private Boolean smoothBoolean;
  private String title;
  private Integer value;
  private Integer width;

  // TODO test this special case
  private Integer widthConstraint;
  @JsonProperty(value = "widthConstraint.maximum")
  private Integer widthConstraintMaximum;

  private Edges(Builder builder) {
    this.arrowsObject = builder.arrowsObject;
    this.arrowsStr = builder.arrowsStr;
    this.arrowStrikethrough = builder.arrowStrikethrough;
    this.chosen = builder.chosen;
    this.colorObject = builder.colorObject;
    this.colorStr = builder.colorStr;
    this.dashesArray = builder.dashesArray;
    this.dashesBoolean = builder.dashesBoolean;
    this.fontObject = builder.fontObject;
    this.fontStr = builder.fontStr;
    this.hidden = builder.hidden;
    this.hoverWidth = builder.hoverWidth;
    this.label = builder.label;
    this.labelHighlightBold = builder.labelHighlightBold;
    this.length = builder.length;
    this.physics = builder.physics;
    this.scaling = builder.scaling;
    this.selectionWidth = builder.selectionWidth;
    this.selfReferenceSize = builder.selfReferenceSize;
    this.shadowObject = builder.shadowObject;
    this.shadowBoolean = builder.shadowBoolean;
    this.smoothObject = builder.smoothObject;
    this.smoothBoolean = builder.smoothBoolean;
    this.title = builder.title;
    this.value = builder.value;
    this.width = builder.width;
    this.widthConstraint = builder.widthConstraint;
    this.widthConstraintMaximum = builder.widthConstraintMaximum;
  }

  public Edges() {}

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }

  public Boolean isArrowStrikethrough() {
    return arrowStrikethrough;
  }

  public void setArrowStrikethrough(Boolean arrowStrikethrough) {
    this.arrowStrikethrough = arrowStrikethrough;
  }

  public Boolean isChosen() {
    return chosen;
  }

  public void setChosen(Boolean chosen) {
    this.chosen = chosen;
  }

  @JsonGetter
  public Object getColor() {
    return ObjectUtils.firstNonNull(colorObject, colorStr);
  }

  public EdgeColor getColorObject() {
    return colorObject;
  }

  public void setColor(EdgeColor colorObject) {
    this.colorObject = colorObject;
    this.colorStr = null;
  }

  public String getColorStr() {
    return colorStr;
  }

  public void setColor(String colorStr) {
    this.colorStr = colorStr;
    this.colorObject = null;
  }

  @JsonGetter
  public Object getDashes() {
    return ObjectUtils.firstNonNull(dashesArray, dashesBoolean);
  }

  public Integer[] getDashesArray() {
    return dashesArray;
  }

  public Boolean getDashesBoolean() {
    return dashesBoolean;
  }

  public void setDashes(Integer[] dashes) {
    this.dashesArray = dashes;
    this.dashesBoolean = null;
  }

  public void setDashes(Boolean dashes) {
    this.dashesBoolean = dashes;
    this.dashesArray = null;
  }

  @JsonGetter
  public Object getFont() {
    return ObjectUtils.firstNonNull(fontObject, fontStr);
  }

  public Font getFontObject() {
    return fontObject;
  }

  public String getFontStr() {
    return fontStr;
  }

  public void setFont(Font font) {
    this.fontObject = font;
    this.fontStr = null;
  }

  public void setFont(String font) {
    this.fontStr = font;
    this.fontObject = null;
  }

  @JsonGetter
  public Object getArrows() {
    return ObjectUtils.firstNonNull(arrowsObject, arrowsStr);
  }

  public Arrows getArrowsObject() {
    return arrowsObject;
  }

  public String getArrowsStr() {
    return arrowsStr;
  }

  public void setArrows(Arrows arrowsObject) {
    this.arrowsObject = arrowsObject;
    this.arrowsStr = null;
  }

  public void setArrows(String arrowsStr) {
    this.arrowsStr = arrowsStr;
    this.arrowsObject = null;
  }

  public Boolean isHidden() {
    return hidden;
  }

  public void setHidden(Boolean hidden) {
    this.hidden = hidden;
  }

  public Double getHoverWidth() {
    return hoverWidth;
  }

  public void setHoverWidth(Double hoverWidth) {
    this.hoverWidth = hoverWidth;
  }

  public Boolean isLabelHighlightBold() {
    return labelHighlightBold;
  }

  public void setLabelHighlightBold(Boolean labelHighlightBold) {
    this.labelHighlightBold = labelHighlightBold;
  }

  public Boolean isPhysics() {
    return physics;
  }

  public void setPhysics(Boolean physics) {
    this.physics = physics;
  }

  public Scaling getScaling() {
    return scaling;
  }

  public void setScaling(Scaling scaling) {
    this.scaling = scaling;
  }

  public Integer getSelectionWidth() {
    return selectionWidth;
  }

  public void setSelectionWidth(Integer selectionWidth) {
    this.selectionWidth = selectionWidth;
  }

  public Integer getSelfReferenceSize() {
    return selfReferenceSize;
  }

  public void setSelfReferenceSize(Integer selfReferenceSize) {
    this.selfReferenceSize = selfReferenceSize;
  }

  @JsonGetter
  public Object getShadow() {
    return ObjectUtils.firstNonNull(shadowObject, shadowBoolean);
  }

  public Shadow getShadowObject() {
    return shadowObject;
  }

  public Boolean getShadowBoolean() {
    return shadowBoolean;
  }

  public void setShadow(Shadow shadow) {
    this.shadowObject = shadow;
    this.shadowBoolean = null;
  }

  public void setShadow(Boolean shadow) {
    this.shadowObject = null;
    this.shadowBoolean = shadow;
  }

  @JsonGetter
  public Object getSmooth() {
    return ObjectUtils.firstNonNull(smoothObject, smoothBoolean);
  }

  public Smooth getSmoothObject() {
    return smoothObject;
  }

  public Boolean getSmoothBoolean() {
    return smoothBoolean;
  }

  public void setSmooth(Smooth smooth) {
    this.smoothObject = smooth;
    this.smoothBoolean = null;
  }

  public void setSmooth(Boolean smooth) {
    this.smoothObject = null;
    this.smoothBoolean = smooth;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getWidthConstraint() {
    return widthConstraint;
  }

  public void setWidthConstraint(Integer widthConstraint) {
    this.widthConstraint = widthConstraint;
  }

  public Integer getWidthConstraintMaximum() {
    return widthConstraintMaximum;
  }

  public void setWidthConstraintMaximum(Integer widthConstraintMaximum) {
    this.widthConstraintMaximum = widthConstraintMaximum;
  }

  /**
   * Creates builder to build {@link Edges}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Edges}.
   */
  public static final class Builder {
    private Arrows arrowsObject;
    private String arrowsStr;
    private Boolean arrowStrikethrough;
    private Boolean chosen;
    private EdgeColor colorObject;
    private String colorStr;
    private Integer[] dashesArray;
    private Boolean dashesBoolean;
    private Font fontObject;
    private String fontStr;
    private Boolean hidden;
    private Double hoverWidth;
    private String label;
    private Boolean labelHighlightBold;
    private Integer length;
    private Boolean physics;
    private Scaling scaling;
    private Integer selectionWidth;
    private Integer selfReferenceSize;
    private Shadow shadowObject;
    private Boolean shadowBoolean;
    private Smooth smoothObject;
    private Boolean smoothBoolean;
    private String title;
    private Integer value;
    private Integer width;
    private Integer widthConstraint;
    private Integer widthConstraintMaximum;

    private Builder() {}

    @Nonnull
    public Builder withArrows(Arrows arrowsObject) {
      this.arrowsObject = arrowsObject;
      return this;
    }

    @Nonnull
    public Builder withArrows(String arrowsStr) {
      this.arrowsStr = arrowsStr;
      return this;
    }

    @Nonnull
    public Builder withArrowStrikethrough(Boolean arrowStrikethrough) {
      this.arrowStrikethrough = arrowStrikethrough;
      return this;
    }

    @Nonnull
    public Builder withChosen(Boolean chosen) {
      this.chosen = chosen;
      return this;
    }

    @Nonnull
    public Builder withColor(EdgeColor colorObject) {
      this.colorObject = colorObject;
      return this;
    }

    @Nonnull
    public Builder withColor(String colorStr) {
      this.colorStr = colorStr;
      return this;
    }

    @Nonnull
    public Builder withDashes(Integer[] dashesArray) {
      this.dashesArray = dashesArray;
      return this;
    }

    @Nonnull
    public Builder withDashes(Boolean dashesBoolean) {
      this.dashesBoolean = dashesBoolean;
      return this;
    }

    @Nonnull
    public Builder withFont(Font fontObject) {
      this.fontObject = fontObject;
      return this;
    }

    @Nonnull
    public Builder withFont(String fontStr) {
      this.fontStr = fontStr;
      return this;
    }

    @Nonnull
    public Builder withHidden(Boolean hidden) {
      this.hidden = hidden;
      return this;
    }

    @Nonnull
    public Builder withHoverWidth(Double hoverWidth) {
      this.hoverWidth = hoverWidth;
      return this;
    }

    @Nonnull
    public Builder withLabel(String label) {
      this.label = label;
      return this;
    }

    @Nonnull
    public Builder withLabelHighlightBold(Boolean labelHighlightBold) {
      this.labelHighlightBold = labelHighlightBold;
      return this;
    }

    @Nonnull
    public Builder withLength(Integer length) {
      this.length = length;
      return this;
    }

    @Nonnull
    public Builder withPhysics(Boolean physics) {
      this.physics = physics;
      return this;
    }

    @Nonnull
    public Builder withScaling(Scaling scaling) {
      this.scaling = scaling;
      return this;
    }

    @Nonnull
    public Builder withSelectionWidth(Integer selectionWidth) {
      this.selectionWidth = selectionWidth;
      return this;
    }

    @Nonnull
    public Builder withSelfReferenceSize(Integer selfReferenceSize) {
      this.selfReferenceSize = selfReferenceSize;
      return this;
    }

    @Nonnull
    public Builder withShadow(Shadow shadowObject) {
      this.shadowObject = shadowObject;
      return this;
    }

    @Nonnull
    public Builder withShadow(Boolean shadowBoolean) {
      this.shadowBoolean = shadowBoolean;
      return this;
    }

    @Nonnull
    public Builder withSmooth(Smooth smoothObject) {
      this.smoothObject = smoothObject;
      return this;
    }

    @Nonnull
    public Builder withSmooth(Boolean smoothBoolean) {
      this.smoothBoolean = smoothBoolean;
      return this;
    }

    @Nonnull
    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    @Nonnull
    public Builder withValue(Integer value) {
      this.value = value;
      return this;
    }

    @Nonnull
    public Builder withWidth(Integer width) {
      this.width = width;
      return this;
    }

    @Nonnull
    public Builder withWidthConstraint(Integer widthConstraint) {
      this.widthConstraint = widthConstraint;
      return this;
    }

    @Nonnull
    public Builder withWidthConstraintMaximum(Integer widthConstraintMaximum) {
      this.widthConstraintMaximum = widthConstraintMaximum;
      return this;
    }

    @Nonnull
    public Edges build() {
      return new Edges(this);
    }
  }

}
