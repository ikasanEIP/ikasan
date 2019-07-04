package org.ikasan.vaadin.visjs.network.util;

/**
 * Created by Martin Prause 9.8.2017
 */

public class ShapeProperties {
  private boolean borderDashes = false; // only for borders
  private int borderRadius = 6; // only for box shape
  private boolean interpolation = false; // only for image and circularImage shapes
  private boolean useImageSize = false; // only for image and circularImage shapes
  private boolean useBorderWithImage = false; // only for image shape

  public boolean isBorderDashes() {
    return borderDashes;
  }

  public void setBorderDashes(boolean borderDashes) {
    this.borderDashes = borderDashes;
  }

  public int getBorderRadius() {
    return borderRadius;
  }

  public void setBorderRadius(int borderRadius) {
    this.borderRadius = borderRadius;
  }

  public boolean isInterpolation() {
    return interpolation;
  }

  public void setInterpolation(boolean interpolation) {
    this.interpolation = interpolation;
  }

  public boolean isUseImageSize() {
    return useImageSize;
  }

  public void setUseImageSize(boolean useImageSize) {
    this.useImageSize = useImageSize;
  }

  public boolean isUseBorderWithImage() {
    return useBorderWithImage;
  }

  public void setUseBorderWithImage(boolean useBorderWithImage) {
    this.useBorderWithImage = useBorderWithImage;
  }

}
