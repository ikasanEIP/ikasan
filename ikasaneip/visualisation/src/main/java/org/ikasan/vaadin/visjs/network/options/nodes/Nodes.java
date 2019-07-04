package org.ikasan.vaadin.visjs.network.options.nodes;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.ikasan.vaadin.visjs.network.util.Fixed;
import org.ikasan.vaadin.visjs.network.util.Font;
import org.ikasan.vaadin.visjs.network.util.Scaling;
import org.ikasan.vaadin.visjs.network.util.Shadow;
import org.ikasan.vaadin.visjs.network.util.Shape;
import org.ikasan.vaadin.visjs.network.util.ShapeProperties;

/**
 */
@JsonDeserialize(builder = Nodes.Builder.class)
public class Nodes {

  private Integer borderWidth;
  private Integer borderWidthSelected;
  private Boolean chosen;
  // Color needs special handling for object versus string
  @JsonIgnore
  private NodeColor color;
  @JsonIgnore
  private String colorStr;
  private Fixed fixed;
  private Font font;
  // group
  private Boolean heightConstraint;
  private Boolean hidden;
  private Icon icon;
  private String image;
  private String brokenImage;
  private String group;
  private String label;
  private Boolean labelHighlightBold;
  private Integer level;
  private Integer mass;
  private Boolean physics;
  private Scaling scaling;
  private Shadow shadow;
  private Shape shape;
  private ShapeProperties shapeProperties;

  private Integer size;
  private String title;
  private String value;
  private WidthConstraint widthConstraint;
  private Integer x;
  private Integer y;

  protected Nodes(Builder builder) {
    this.borderWidth = builder.borderWidth;
    this.borderWidthSelected = builder.borderWidthSelected;
    this.chosen = builder.chosen;
    this.color = builder.color;
    this.colorStr = builder.colorStr;
    this.fixed = builder.fixed;
    this.font = builder.font;
    this.heightConstraint = builder.heightConstraint;
    this.hidden = builder.hidden;
    this.icon = builder.icon;
    this.image = builder.image;
    this.brokenImage = builder.brokenImage;
    this.group = builder.group;
    this.label = builder.label;
    this.labelHighlightBold = builder.labelHighlightBold;
    this.level = builder.level;
    this.mass = builder.mass;
    this.physics = builder.physics;
    this.scaling = builder.scaling;
    this.shadow = builder.shadow;
    this.shape = builder.shape;
    this.shapeProperties = builder.shapeProperties;
    this.size = builder.size;
    this.title = builder.title;
    this.value = builder.value;
    this.widthConstraint = builder.widthConstraint;
    this.x = builder.x;
    this.y = builder.y;
  }

  public Nodes() {}

  public boolean isChosen() {
    return chosen;
  }

  public void setChosen(final boolean chosen) {
    this.chosen = chosen;
  }

  public WidthConstraint getWidthConstraint() {
    return widthConstraint;
  }

  public Integer getLevel() {
    return level;
  }

  public void setLevel(final Integer level) {
    this.level = level;
  }

  public int getBorderWidth() {
    return borderWidth;
  }

  public void setBorderWidth(final int borderWidth) {
    this.borderWidth = borderWidth;
  }

  public int getBorderWidthSelected() {
    return borderWidthSelected;
  }

  public void setBorderWidthSelected(final int borderWidthSelected) {
    this.borderWidthSelected = borderWidthSelected;
  }

  /**
   * if set colorStr returns
   *
   * @return colorStr if set, else color else null
   */
  @JsonGetter(value = "color")
  protected Object getColorJson() {
    if (color != null) {
      return color;
    } else if (colorStr != null) {
      return colorStr;
    }
    return null;
  }

  public String getColorStr() {
    return colorStr;
  }

  public NodeColor getColor() {
    return color;
  }

  public void setColor(final NodeColor color) {
    this.color = color;
    this.colorStr = null;
  }

  public void setColor(final String color) {
    this.colorStr = color;
    this.color = null;
  }

  public Font getFont() {
    return font;
  }

  public void setFont(final Font font) {
    this.font = font;
  }

  public boolean isHeightConstraint() {
    return heightConstraint;
  }

  public void setHeightConstraint(final boolean heightConstraint) {
    this.heightConstraint = heightConstraint;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden(final boolean hidden) {
    this.hidden = hidden;
  }

  public Icon getIcon() {
    return icon;
  }

  public void setIcon(final Icon icon) {
    this.icon = icon;
  }

  public String getImage() {
    return image;
  }

  public void setImage(final String image) {
    this.image = image;
  }

  public String getBrokenImage() {
    return brokenImage;
  }

  public void setBrokenImage(final String brokenImage) {
    this.brokenImage = brokenImage;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isLabelHighlightBold() {
    return labelHighlightBold;
  }

  public void setLabelHighlightBold(final boolean labelHighlightBold) {
    this.labelHighlightBold = labelHighlightBold;
  }

  public int getMass() {
    return mass;
  }

  public void setMass(final int mass) {
    this.mass = mass;
  }

  public boolean isPhysics() {
    return physics;
  }

  public void setPhysics(final boolean physics) {
    this.physics = physics;
  }

  public Scaling getScaling() {
    return scaling;
  }

  public void setScaling(final Scaling scaling) {
    this.scaling = scaling;
  }

  public Shadow getShadow() {
    return shadow;
  }

  public void setShadow(final Shadow shadow) {
    this.shadow = shadow;
  }

  public Shape getShape() {
    return shape;
  }

  public void setShape(final Shape shape) {
    this.shape = shape;
  }

  public ShapeProperties getShapeProperties() {
    return shapeProperties;
  }

  public void setShapeProperties(final ShapeProperties shapeProperties) {
    this.shapeProperties = shapeProperties;
  }

  public int getSize() {
    return size;
  }

  public void setSize(final int size) {
    this.size = size;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getValue() {
    return value;
  }

  public void setValue(final String value) {
    this.value = value;
  }

  public WidthConstraint isWidthConstraint() {
    return widthConstraint;
  }

  public void setWidthConstraint(final WidthConstraint widthConstraint) {
    this.widthConstraint = widthConstraint;
  }

  public Integer getX() {
    return x;
  }

  public void setX(final Integer x) {
    this.x = x;
  }

  public Integer getY() {
    return y;
  }

  public void setY(final Integer y) {
    this.y = y;
  }

  public Fixed getFixed() {
    return fixed;
  }

  public void setFixed(final Fixed fixed) {
    this.fixed = fixed;
  }

  /**
   * Creates builder to build {@link Nodes}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Nodes}.
   */
  public static class Builder {
    private Integer borderWidth;
    private Integer borderWidthSelected;
    private Boolean chosen;
    private NodeColor color;
    private String colorStr;
    private Fixed fixed;
    private Font font;
    private Boolean heightConstraint;
    private Boolean hidden;
    private Icon icon;
    private String image;
    private String brokenImage;
    private String group;
    private String label;
    private Boolean labelHighlightBold;
    private Integer level;
    private Integer mass;
    private Boolean physics;
    private Scaling scaling;
    private Shadow shadow;
    private Shape shape;
    private ShapeProperties shapeProperties;
    private Integer size;
    private String title;
    private String value;
    private WidthConstraint widthConstraint;
    private Integer x;
    private Integer y;

    protected Builder() {}

    @Nonnull
    public Builder withBorderWidth(Integer borderWidth) {
      this.borderWidth = borderWidth;
      return this;
    }

    @Nonnull
    public Builder withBorderWidthSelected(Integer borderWidthSelected) {
      this.borderWidthSelected = borderWidthSelected;
      return this;
    }

    @Nonnull
    public Builder withChosen(Boolean chosen) {
      this.chosen = chosen;
      return this;
    }

    @Nonnull
    public Builder withColor(NodeColor color) {
      this.color = color;
      return this;
    }

    @Nonnull
    public Builder withColorStr(String colorStr) {
      this.colorStr = colorStr;
      return this;
    }

    @Nonnull
    public Builder withFixed(Fixed fixed) {
      this.fixed = fixed;
      return this;
    }

    @Nonnull
    public Builder withFont(Font font) {
      this.font = font;
      return this;
    }

    @Nonnull
    public Builder withGroup(String group) {
      this.group = group;
      return this;
    }

    @Nonnull
    public Builder withLabel(String label) {
      this.label = label;
      return this;
    }

    @Nonnull
    public Builder withHeightConstraint(Boolean heightConstraint) {
      this.heightConstraint = heightConstraint;
      return this;
    }

    @Nonnull
    public Builder withHidden(Boolean hidden) {
      this.hidden = hidden;
      return this;
    }

    @Nonnull
    public Builder withIcon(Icon icon) {
      this.icon = icon;
      return this;
    }

    @Nonnull
    public Builder withImage(String image) {
      this.image = image;
      return this;
    }

    @Nonnull
    public Builder withBrokenImage(String brokenImage) {
      this.brokenImage = brokenImage;
      return this;
    }

    @Nonnull
    public Builder withLabelHighlightBold(Boolean labelHighlightBold) {
      this.labelHighlightBold = labelHighlightBold;
      return this;
    }

    @Nonnull
    public Builder withLevel(Integer level) {
      this.level = level;
      return this;
    }

    @Nonnull
    public Builder withMass(Integer mass) {
      this.mass = mass;
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
    public Builder withShadow(Shadow shadow) {
      this.shadow = shadow;
      return this;
    }

    @Nonnull
    public Builder withShape(Shape shape) {
      this.shape = shape;
      return this;
    }

    @Nonnull
    public Builder withShapeProperties(ShapeProperties shapeProperties) {
      this.shapeProperties = shapeProperties;
      return this;
    }

    @Nonnull
    public Builder withSize(Integer size) {
      this.size = size;
      return this;
    }

    @Nonnull
    public Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    @Nonnull
    public Builder withValue(String value) {
      this.value = value;
      return this;
    }

    @Nonnull
    public Builder withWidthConstraint(WidthConstraint widthConstraint) {
      this.widthConstraint = widthConstraint;
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
    public Nodes build() {
      return new Nodes(this);
    }
  }
}
