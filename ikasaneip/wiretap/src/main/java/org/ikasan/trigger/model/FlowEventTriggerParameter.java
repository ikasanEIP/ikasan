package org.ikasan.trigger.model;

import jakarta.persistence.*;

@Entity
@Table(name = "FlowEventTriggerParameters")
public class FlowEventTriggerParameter {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long triggerId;
    @Column(name="ParamName", nullable = false)
    private String paramName;
    @Column(name="ParamValue", nullable = false)
    private String paramValue;

    public long getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(long triggerId) {
        this.triggerId = triggerId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }
}
