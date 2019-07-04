package org.ikasan.vaadin.visjs.network.util;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * Used by nodes and edges.
 *
 * @author watho
 *
 */
@JsonDeserialize(builder = Font.Builder.class)
public class Font {

  String color;
  Integer size;
  String face;
  String background;
  Integer strokeWidth;
  String strokeColor;
  String align;
  String vadjust;
  @JsonIgnore
  String multiStr;
  @JsonIgnore
  Boolean multiBoolean;
  @JsonIgnore
  String boldStr;
  @JsonIgnore
  FontStyle boldStyle;
  @JsonIgnore
  String italStr;
  @JsonIgnore
  FontStyle italStyle;
  @JsonIgnore
  String bolditalStr;
  @JsonIgnore
  FontStyle bolditalStyle;
  @JsonIgnore
  String monoStr;
  @JsonIgnore
  FontStyle monoStyle;

  private Font(Builder builder) {
    this.color = builder.color;
    this.size = builder.size;
    this.face = builder.face;
    this.background = builder.background;
    this.strokeWidth = builder.strokeWidth;
    this.strokeColor = builder.strokeColor;
    this.align = builder.align;
    this.vadjust = builder.vadjust;
    this.multiStr = builder.multiStr;
    this.multiBoolean = builder.multiBoolean;
    this.boldStr = builder.boldStr;
    this.boldStyle = builder.boldStyle;
    this.italStr = builder.italStr;
    this.italStyle = builder.italStyle;
    this.bolditalStr = builder.bolditalStr;
    this.bolditalStyle = builder.bolditalStyle;
    this.monoStr = builder.monoStr;
    this.monoStyle = builder.monoStyle;
  }

  public Font() {}

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

  public String getBackground() {
    return background;
  }

  public void setBackground(String background) {
    this.background = background;
  }

  public int getStrokeWidth() {
    return strokeWidth;
  }

  public void setStrokeWidth(int strokeWidth) {
    this.strokeWidth = strokeWidth;
  }

  public String getStrokeColor() {
    return strokeColor;
  }

  public void setStrokeColor(String strokeColor) {
    this.strokeColor = strokeColor;
  }

  public String getAlign() {
    return align;
  }

  public void setAlign(String align) {
    this.align = align;
  }

  public String getVadjust() {
    return vadjust;
  }

  public void setVadjust(String vadjust) {
    this.vadjust = vadjust;
  }

  @JsonGetter
  protected Object getMulti() {
    return ObjectUtils.firstNonNull(multiBoolean, monoStr);
  }

  public void setMulti(String multiStr) {
    this.multiStr = multiStr;
    this.multiBoolean = null;
  }

  public void setMulti(Boolean multiBoolean) {
    this.multiBoolean = multiBoolean;
    this.multiStr = null;
  }

  @JsonGetter
  public Object getBold() {
    if (boldStyle != null) {
      return boldStyle;
    } else if (boldStr != null) {
      return boldStr;
    }
    return null;
  }

  public void setBold(FontStyle boldStyle) {
    this.boldStyle = boldStyle;
    this.boldStr = null;
  }

  public void setBold(String boldStr) {
    this.boldStr = boldStr;
    this.boldStyle = null;
  }

  @JsonGetter
  public Object getItal() {
    if (italStyle != null) {
      return italStyle;
    } else if (italStr != null) {
      return italStr;
    }
    return null;
  }

  public void setItal(FontStyle italStyle) {
    this.italStyle = italStyle;
    this.italStr = null;
  }

  public void setItal(String italStr) {
    this.italStr = italStr;
    this.italStyle = null;
  }

  @JsonGetter
  public Object getBoldital() {
    if (bolditalStyle != null) {
      return bolditalStyle;
    } else if (bolditalStr != null) {
      return bolditalStr;
    }
    return null;
  }

  public void setBoldital(FontStyle bolditalStyle) {
    this.bolditalStyle = bolditalStyle;
    this.bolditalStr = null;
  }

  public void setBoldital(String bolditalStr) {
    this.bolditalStr = bolditalStr;
    this.bolditalStyle = null;
  }

  @JsonGetter
  public Object getMono() {
    if (monoStyle != null) {
      return monoStyle;
    } else if (monoStr != null) {
      return monoStr;
    }
    return null;
  }

  public void setMono(FontStyle monoStyle) {
    this.monoStyle = monoStyle;
    this.monoStr = null;
  }

  public void setMono(String monoStr) {
    this.monoStr = monoStr;
    this.monoStyle = null;
  }

  /**
   * Creates builder to build {@link Font}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Font}.
   */
  public static final class Builder {
    private String color;
    private Integer size;
    private String face;
    private String background;
    private Integer strokeWidth;
    private String strokeColor;
    private String align;
    private String vadjust;
    private String multiStr;
    private Boolean multiBoolean;
    private String boldStr;
    private FontStyle boldStyle;
    private String italStr;
    private FontStyle italStyle;
    private String bolditalStr;
    private FontStyle bolditalStyle;
    private String monoStr;
    private FontStyle monoStyle;

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
    public Builder withBackground(String background) {
      this.background = background;
      return this;
    }

    @Nonnull
    public Builder withStrokeWidth(Integer strokeWidth) {
      this.strokeWidth = strokeWidth;
      return this;
    }

    @Nonnull
    public Builder withStrokeColor(String strokeColor) {
      this.strokeColor = strokeColor;
      return this;
    }

    @Nonnull
    public Builder withAlign(String align) {
      this.align = align;
      return this;
    }

    @Nonnull
    public Builder withVadjust(String vadjust) {
      this.vadjust = vadjust;
      return this;
    }

    @Nonnull
    public Builder withMultiStr(String multiStr) {
      this.multiStr = multiStr;
      return this;
    }

    @Nonnull
    public Builder withMultiBoolean(Boolean multiBoolean) {
      this.multiBoolean = multiBoolean;
      return this;
    }

    @Nonnull
    public Builder withBoldStr(String boldStr) {
      this.boldStr = boldStr;
      return this;
    }

    @Nonnull
    public Builder withBoldStyle(FontStyle boldStyle) {
      this.boldStyle = boldStyle;
      return this;
    }

    @Nonnull
    public Builder withItalStr(String italStr) {
      this.italStr = italStr;
      return this;
    }

    @Nonnull
    public Builder withItalStyle(FontStyle italStyle) {
      this.italStyle = italStyle;
      return this;
    }

    @Nonnull
    public Builder withBolditalStr(String bolditalStr) {
      this.bolditalStr = bolditalStr;
      return this;
    }

    @Nonnull
    public Builder withBolditalStyle(FontStyle bolditalStyle) {
      this.bolditalStyle = bolditalStyle;
      return this;
    }

    @Nonnull
    public Builder withMonoStr(String monoStr) {
      this.monoStr = monoStr;
      return this;
    }

    @Nonnull
    public Builder withMonoStyle(FontStyle monoStyle) {
      this.monoStyle = monoStyle;
      return this;
    }

    @Nonnull
    public Font build() {
      return new Font(this);
    }
  }

}
