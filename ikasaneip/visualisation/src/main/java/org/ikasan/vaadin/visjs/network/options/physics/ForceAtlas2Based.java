package org.ikasan.vaadin.visjs.network.options.physics;

/**
 * Created by Martin Prause 4.8.2017
 */
public class ForceAtlas2Based {

  int gravitationalConstant = -50;
  int springLength = 100;
  float centralGravity = 0.1f;
  float springConstant = 0.08f;
  float damping = 0.4f;
  float avoidOverlap = 0.0f;

  public int getGravitationalConstant() {
    return gravitationalConstant;
  }

  public void setGravitationalConstant(int gravitationalConstant) {
    this.gravitationalConstant = gravitationalConstant;
  }

  public int getSpringLength() {
    return springLength;
  }

  public void setSpringLength(int springLength) {
    this.springLength = springLength;
  }

  public float getCentralGravity() {
    return centralGravity;
  }

  public void setCentralGravity(float centralGravity) {
    this.centralGravity = centralGravity;
  }

  public float getSpringConstant() {
    return springConstant;
  }

  public void setSpringConstant(float springConstant) {
    this.springConstant = springConstant;
  }

  public float getDamping() {
    return damping;
  }

  public void setDamping(float damping) {
    this.damping = damping;
  }

  public float getAvoidOverlap() {
    return avoidOverlap;
  }

  public void setAvoidOverlap(float avoidOverlap) {
    this.avoidOverlap = avoidOverlap;
  }

}
