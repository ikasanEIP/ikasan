package org.ikasan.history.model;

import jakarta.persistence.*;
import org.ikasan.spec.history.FlowInvocationMetric;

import java.io.Serializable;
import java.util.Set;

/**
 * @author Ikasan Development Team
 */
@Entity
@Table(name = "FlowInvocationMetric")
public class FlowInvocationMetricImpl implements FlowInvocationMetric<ComponentInvocationMetricImpl>, Serializable
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    @Column(name="ModuleName", nullable = false)
    private String moduleName;
    @Column(name="FlowName", nullable = false)
    private String flowName;
    @Column(name="StartTime", nullable = false)
    private long invocationStartTime;
    @Column(name="EndTime", nullable = true)
    private long invocationEndTime;
    @Column(name="FinalAction", nullable = false)
    private String finalAction;
    @OneToMany(mappedBy="flowInvocation", fetch = FetchType.EAGER)
    private Set<ComponentInvocationMetricImpl> componentInvocationMetricImpls;
    @Column(name="Harvested", nullable = false)
    private Boolean harvested = false;
    @Column(name="Expiry", nullable = false)
    private long expiry;
    @Column(name="ErrorUri", nullable = true)
    private String errorUri;
    /** the time the record was harvested */
    @Column(name="HarvestedDateTime", nullable = false)
    private long harvestedDateTime = 0L;

    /**
     * Constructor
     *
     * @param moduleName
     * @param flowName
     * @param invocationStartTime
     * @param invocationEndTime
     * @param finalAction
     * @param componentInvocationMetricImpls
     * @param expiry
     */
    public FlowInvocationMetricImpl(String moduleName, String flowName, long invocationStartTime, long invocationEndTime
            , String finalAction, Set<ComponentInvocationMetricImpl> componentInvocationMetricImpls, long expiry, String errorUri)
    {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.invocationStartTime = invocationStartTime;
        this.invocationEndTime = invocationEndTime;
        this.finalAction = finalAction;
        this.componentInvocationMetricImpls = componentInvocationMetricImpls;
        this.expiry = expiry;
        this.errorUri = errorUri;
    }

    protected FlowInvocationMetricImpl()
    {

    }

    public Long getId()
    {
        return id;
    }

    private void setId(Long id)
    {
        this.id = id;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getFlowName()
    {
        return flowName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public long getInvocationStartTime()
    {
        return invocationStartTime;
    }

    public void setInvocationStartTime(long invocationStartTime)
    {
        this.invocationStartTime = invocationStartTime;
    }

    public long getInvocationEndTime()
    {
        return invocationEndTime;
    }

    public void setInvocationEndTime(long invocationEndTime)
    {
        this.invocationEndTime = invocationEndTime;
    }

    public String getFinalAction()
    {
        return finalAction;
    }

    public void setFinalAction(String finalAction)
    {
        this.finalAction = finalAction;
    }

    @Override
    public Set<ComponentInvocationMetricImpl> getFlowInvocationEvents()
    {
        return this.componentInvocationMetricImpls;
    }

    @Override
    public void setFlowInvocationEvents(Set<ComponentInvocationMetricImpl> componentInvocationMetricImpls)
    {
        this.componentInvocationMetricImpls = componentInvocationMetricImpls;
        this.componentInvocationMetricImpls.forEach(componentInvocationMetric -> componentInvocationMetric.setFlowInvocation(this));
    }

    public Boolean getHarvested()
    {
        return harvested;
    }

    public void setHarvested(Boolean harvested)
    {
        this.harvested = harvested;
    }

    public long getExpiry()
    {
        return expiry;
    }

    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    public Set<ComponentInvocationMetricImpl> getComponentInvocationMetricImpls()
    {
        return componentInvocationMetricImpls;
    }

    public void setComponentInvocationMetricImpls(Set<ComponentInvocationMetricImpl> componentInvocationMetricImpls)
    {
        this.componentInvocationMetricImpls = componentInvocationMetricImpls;
    }

    @Override
    public String getErrorUri()
    {
        return errorUri;
    }

    @Override
    public void setErrorUri(String errorUri)
    {
        this.errorUri = errorUri;
    }

    public long getHarvestedDateTime()
    {
        return harvestedDateTime;
    }

    public void setHarvestedDateTime(long harvestedDateTime)
    {
        this.harvestedDateTime = harvestedDateTime;
    }
}
