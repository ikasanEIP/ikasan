package org.ikasan.flow.configuration;

public class FlowComponentInvokerConfiguration {
    private String flowName;
    private String componentName;
    private boolean captureMetrics = false;
    private boolean snapEvent = false;
    private boolean dynamicConfiguration = false;

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public boolean isCaptureMetrics() {
        return captureMetrics;
    }

    public void setCaptureMetrics(boolean captureMetrics) {
        this.captureMetrics = captureMetrics;
    }

    public boolean isSnapEvent() {
        return snapEvent;
    }

    public void setSnapEvent(boolean snapEvent) {
        this.snapEvent = snapEvent;
    }

    public boolean isDynamicConfiguration() {
        return dynamicConfiguration;
    }

    public void setDynamicConfiguration(boolean dynamicConfiguration) {
        this.dynamicConfiguration = dynamicConfiguration;
    }
}
