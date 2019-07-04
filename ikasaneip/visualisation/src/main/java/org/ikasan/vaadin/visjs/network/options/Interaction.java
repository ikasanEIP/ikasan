package org.ikasan.vaadin.visjs.network.options;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = Interaction.Builder.class)
public class Interaction {

  private Boolean dragNodes;
  private Boolean dragView;
  private Boolean hideEdgesOnDrag;
  private Boolean hideNodesOnDrag;
  private Boolean hover;
  private Boolean hoverConnectedEdges;
  private Boolean multiselect;
  private Boolean navigationButtons;
  private Boolean selectable;
  private Boolean selectConnectedEdges;
  private Integer tooltipDelay;
  private Boolean zoomView;

  private Interaction(Builder builder) {
    this.dragNodes = builder.dragNodes;
    this.dragView = builder.dragView;
    this.hideEdgesOnDrag = builder.hideEdgesOnDrag;
    this.hideNodesOnDrag = builder.hideNodesOnDrag;
    this.hover = builder.hover;
    this.hoverConnectedEdges = builder.hoverConnectedEdges;
    this.multiselect = builder.multiselect;
    this.navigationButtons = builder.navigationButtons;
    this.selectable = builder.selectable;
    this.selectConnectedEdges = builder.selectConnectedEdges;
    this.tooltipDelay = builder.tooltipDelay;
    this.zoomView = builder.zoomView;
  }

  public Interaction() {}

  public boolean isDragNodes() {
    return dragNodes;
  }

  public void setDragNodes(boolean dragNodes) {
    this.dragNodes = dragNodes;
  }

  public boolean isDragView() {
    return dragView;
  }

  public void setDragView(boolean dragView) {
    this.dragView = dragView;
  }

  public boolean isHideEdgesOnDrag() {
    return hideEdgesOnDrag;
  }

  public void setHideEdgesOnDrag(boolean hideEdgesOnDrag) {
    this.hideEdgesOnDrag = hideEdgesOnDrag;
  }

  public boolean isHideNodesOnDrag() {
    return hideNodesOnDrag;
  }

  public void setHideNodesOnDrag(boolean hideNodesOnDrag) {
    this.hideNodesOnDrag = hideNodesOnDrag;
  }

  public boolean isHover() {
    return hover;
  }

  public void setHover(boolean hover) {
    this.hover = hover;
  }

  public boolean isHoverConnectedEdges() {
    return hoverConnectedEdges;
  }

  public void setHoverConnectedEdges(boolean hoverConnectedEdges) {
    this.hoverConnectedEdges = hoverConnectedEdges;
  }

  public boolean isMultiselect() {
    return multiselect;
  }

  public void setMultiselect(boolean multiselect) {
    this.multiselect = multiselect;
  }

  public boolean isNavigationButtons() {
    return navigationButtons;
  }

  public void setNavigationButtons(boolean navigationButtons) {
    this.navigationButtons = navigationButtons;
  }

  public boolean isSelectable() {
    return selectable;
  }

  public void setSelectable(boolean selectable) {
    this.selectable = selectable;
  }

  public boolean isSelectConnectedEdges() {
    return selectConnectedEdges;
  }

  public void setSelectConnectedEdges(boolean selectConnectedEdges) {
    this.selectConnectedEdges = selectConnectedEdges;
  }

  public int getTooltipDelay() {
    return tooltipDelay;
  }

  public void setTooltipDelay(int tooltipDelay) {
    this.tooltipDelay = tooltipDelay;
  }

  public boolean isZoomView() {
    return zoomView;
  }

  public void setZoomView(boolean zoomView) {
    this.zoomView = zoomView;
  }

  /**
   * Creates builder to build {@link Interaction}.
   * 
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Interaction}.
   */
  public static final class Builder {
    private Boolean dragNodes;
    private Boolean dragView;
    private Boolean hideEdgesOnDrag;
    private Boolean hideNodesOnDrag;
    private Boolean hover;
    private Boolean hoverConnectedEdges;
    private Boolean multiselect;
    private Boolean navigationButtons;
    private Boolean selectable;
    private Boolean selectConnectedEdges;
    private Integer tooltipDelay;
    private Boolean zoomView;

    private Builder() {}

    @Nonnull
    public Builder withDragNodes(Boolean dragNodes) {
      this.dragNodes = dragNodes;
      return this;
    }

    @Nonnull
    public Builder withDragView(Boolean dragView) {
      this.dragView = dragView;
      return this;
    }

    @Nonnull
    public Builder withHideEdgesOnDrag(Boolean hideEdgesOnDrag) {
      this.hideEdgesOnDrag = hideEdgesOnDrag;
      return this;
    }

    @Nonnull
    public Builder withHideNodesOnDrag(Boolean hideNodesOnDrag) {
      this.hideNodesOnDrag = hideNodesOnDrag;
      return this;
    }

    @Nonnull
    public Builder withHover(Boolean hover) {
      this.hover = hover;
      return this;
    }

    @Nonnull
    public Builder withHoverConnectedEdges(Boolean hoverConnectedEdges) {
      this.hoverConnectedEdges = hoverConnectedEdges;
      return this;
    }

    @Nonnull
    public Builder withMultiselect(Boolean multiselect) {
      this.multiselect = multiselect;
      return this;
    }

    @Nonnull
    public Builder withNavigationButtons(Boolean navigationButtons) {
      this.navigationButtons = navigationButtons;
      return this;
    }

    @Nonnull
    public Builder withSelectable(Boolean selectable) {
      this.selectable = selectable;
      return this;
    }

    @Nonnull
    public Builder withSelectConnectedEdges(Boolean selectConnectedEdges) {
      this.selectConnectedEdges = selectConnectedEdges;
      return this;
    }

    @Nonnull
    public Builder withTooltipDelay(Integer tooltipDelay) {
      this.tooltipDelay = tooltipDelay;
      return this;
    }

    @Nonnull
    public Builder withZoomView(Boolean zoomView) {
      this.zoomView = zoomView;
      return this;
    }

    @Nonnull
    public Interaction build() {
      return new Interaction(this);
    }
  }

}
