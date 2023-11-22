package org.ikasan.history.model;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "CustomMetric")
public class CustomMetric implements Serializable
{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
	private Long id;
    @ManyToOne
    @JoinColumn(name="CompInvocationMetricId", nullable=false, updatable = false)
    private ComponentInvocationMetricImpl componentInvocationMetricImpl;

    @Column(name="Name", nullable = false)
    private String name;

    @Column(name="CustomValue", nullable = false)
    private String value;

	protected CustomMetric() 
	{
		super();
	}
	
	/**
	 * Constructor 
	 * 
	 * @param name
	 * @param value
	 */
	public CustomMetric(String name, String value) 
	{
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public Long getId() 
	{
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	@SuppressWarnings("unused")
	private void setId(Long id) 
	{
		this.id = id;
	}
	
	/**
	 * @return the messageHistoryFlowEvent
	 */
	public ComponentInvocationMetricImpl getComponentInvocationMetricImpl()
	{
		return componentInvocationMetricImpl;
	}
	
	/**
	 * @param componentInvocationMetricImpl the messageHistoryFlowEvent to set
	 */
	public void setComponentInvocationMetricImpl(ComponentInvocationMetricImpl componentInvocationMetricImpl)
	{
		this.componentInvocationMetricImpl = componentInvocationMetricImpl;
	}
	
	/**
	 * @return the name
	 */
	public String getName() 
	{
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) 
	{
		this.name = name;
	}
	
	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) 
	{
		this.value = value;
	}

}
