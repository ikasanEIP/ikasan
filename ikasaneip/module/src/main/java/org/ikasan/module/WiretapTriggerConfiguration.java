package org.ikasan.module;

public class WiretapTriggerConfiguration {

    public String flowName;

    public String componentName;

    public String beforeOrAfter;

    public String timeToLiveInSeconds;

    public WiretapTriggerConfiguration(String flowName, String componentName, String beforeOrAfter, String timeToLiveInSeconds) {
        this.flowName = flowName;
        this.componentName = componentName;
        this.beforeOrAfter = beforeOrAfter;
        this.timeToLiveInSeconds = timeToLiveInSeconds;
    }

    public WiretapTriggerConfiguration(){

    }

    public final String getFlowName() {
        return flowName;
    }


    public String getComponentName() {
        return componentName;
    }

    public String getBeforeOrAfter() {
        return beforeOrAfter;
    }

    public String getTimeToLiveInSeconds() {
        return timeToLiveInSeconds;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setBeforeOrAfter(String beforeOrAfter) {
        this.beforeOrAfter = beforeOrAfter;
    }

    public void setTimeToLiveInSeconds(String timeToLiveInSeconds) {
        this.timeToLiveInSeconds = timeToLiveInSeconds;
    }

    @Override
    public String toString() {
        return "WiretapTriggerConfiguration{" +
            "flowName='" + flowName + '\'' +
            ", componentName='" + componentName + '\'' +
            ", beforeOrAfter='" + beforeOrAfter + '\'' +
            ", timeToLiveInSeconds='" + timeToLiveInSeconds + '\'' +
            '}';
    }
}
