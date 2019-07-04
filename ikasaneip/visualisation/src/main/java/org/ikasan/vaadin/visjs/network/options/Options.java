package org.ikasan.vaadin.visjs.network.options;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.ikasan.vaadin.visjs.network.options.edges.Edges;
import org.ikasan.vaadin.visjs.network.options.edges.Layout;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.options.physics.Physics;

public class Options {

  private Boolean clickToUse;
  private Boolean autoResize;
  private String locale;
  private String height;
  private String width;
  private Map<String, Locale> locales;
  private Configure configure;
  private Manipulation manipulation;
  private Interaction interaction;
  private Map<String, Nodes> groups;

  private Physics physics;
  private Edges edges;
  private Nodes nodes;
  // private Cluster clustering;
  private Layout layout;

  private Options(Builder builder) {
    this.clickToUse = builder.clickToUse;
    this.autoResize = builder.autoResize;
    this.locale = builder.locale;
    this.height = builder.height;
    this.width = builder.width;
    this.locales = builder.locales;
    this.configure = builder.configure;
    this.manipulation = builder.manipulation;
    this.interaction = builder.interaction;
    this.groups = builder.groups;
    this.physics = builder.physics;
    this.edges = builder.edges;
    this.nodes = builder.nodes;
    this.layout = builder.layout;
  }

  public Options() {}

  public Map<String, Nodes> getGroups() {
    return groups;
  }

  public void setGroups(final Map<String, Nodes> groups) {
    this.groups = groups;
  }

  public Interaction getInteraction() {
    return interaction;
  }

  public void setInteraction(final Interaction interaction) {
    this.interaction = interaction;
  }

  public Boolean isAutoResize() {
    return autoResize;
  }

  public void setAutoResize(final Boolean autoResize) {
    this.autoResize = autoResize;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(final String locale) {
    this.locale = locale;
  }

  public Configure getConfigure() {
    return configure;
  }

  public void setConfigure(final Configure configure) {
    this.configure = configure;
  }

  public Manipulation getManipulation() {
    return manipulation;
  }

  public void setManipulation(final Manipulation manipulation) {
    this.manipulation = manipulation;
  }

  /**
   * Toggle the manipulation system on or off. Even when false, the manipulation API through the
   * methods will still work.
   */
  @JsonIgnore
  public void setManipulationEnabled(final boolean enable) {
    if (getManipulation() == null) {
      setManipulation(new Manipulation());
    }
    getManipulation().setEnabled(enable);
  }

  @JsonIgnore
  public boolean isManipulationEnabled() {
    if (getManipulation() != null) {
      return getManipulation().isEnabled();
    }
    // default
    return false;
  }

  public Boolean isClickToUse() {
    return clickToUse;
  }

  public void setClickToUse(final Boolean clickToUse) {
    this.clickToUse = clickToUse;
  }

  public String getHeight() {
    return height;
  }

  public void setHeight(final String height) {
    this.height = height;
  }

  public String getWidth() {
    return width;
  }

  public void setWidth(final String width) {
    this.width = width;
  }

  public Map<String, Locale> getLocales() {
    return locales;
  }

  public void setLocales(final Map<String, Locale> locales) {
    this.locales = locales;
  }

  public Physics getPhysics() {
    return physics;
  }

  public void setPhysics(final Physics physics) {
    this.physics = physics;
  }

  public Edges getEdges() {
    return edges;
  }

  public void setEdges(final Edges edges) {
    this.edges = edges;
  }

  public Nodes getNodes() {
    return nodes;
  }

  public void setNodes(final Nodes nodes) {
    this.nodes = nodes;
  }

  public Layout getLayout() {
    return layout;
  }

  public void setLayout(final Layout layout) {
    this.layout = layout;
  }

  /**
   * Creates builder to build {@link Options}. For default options for every option not set see
   * <a href="http://visjs.org/docs/network/#options">http://visjs.org/docs/network/#options</a>
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link Options}.
   */
  public static final class Builder {
    private Boolean clickToUse;
    private Boolean autoResize;
    private String locale;
    private String height;
    private String width;
    private Map<String, Locale> locales;
    private Configure configure;
    private Manipulation manipulation;
    private Interaction interaction;
    private Map<String, Nodes> groups;
    private Physics physics;
    private Edges edges;
    private Nodes nodes;
    private Layout layout;

    private Builder() {}

    @Nonnull
    public Builder withClickToUse(Boolean clickToUse) {
      this.clickToUse = clickToUse;
      return this;
    }

    @Nonnull
    public Builder withAutoResize(Boolean autoResize) {
      this.autoResize = autoResize;
      return this;
    }

    @Nonnull
    public Builder withLocale(String locale) {
      this.locale = locale;
      return this;
    }

    @Nonnull
    public Builder withHeight(String height) {
      this.height = height;
      return this;
    }

    @Nonnull
    public Builder withWidth(String width) {
      this.width = width;
      return this;
    }

    @Nonnull
    public Builder withLocales(HashMap<String, Locale> locales) {
      this.locales = locales;
      return this;
    }

    @Nonnull
    public Builder withConfigure(Configure configure) {
      this.configure = configure;
      return this;
    }

    @Nonnull
    public Builder withManipulation(Manipulation manipulation) {
      this.manipulation = manipulation;
      return this;
    }

    @Nonnull
    public Builder withInteraction(Interaction interaction) {
      this.interaction = interaction;
      return this;
    }

    @Nonnull
    public Builder withGroups(Map<String, Nodes> groups) {
      this.groups = groups;
      return this;
    }

    @Nonnull
    public Builder withPhysics(Physics physics) {
      this.physics = physics;
      return this;
    }

    @Nonnull
    public Builder withEdges(Edges edges) {
      this.edges = edges;
      return this;
    }

    @Nonnull
    public Builder withNodes(Nodes nodes) {
      this.nodes = nodes;
      return this;
    }

    @Nonnull
    public Builder withLayout(Layout layout) {
      this.layout = layout;
      return this;
    }

    @Nonnull
    public Options build() {
      return new Options(this);
    }
  }

}
