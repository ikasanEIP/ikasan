package org.ikasan.mapping.model;

import java.util.Set;

/**
 * Created by stewmi on 25/01/2017.
 */
public class SourceValueTargetValueGrouping
{
    private Long id;
    private Long groupingId;
    private Set<ManyToManyTargetConfigurationValue> targetValues;

    /**
     * It is a Hibernate requirement that all properties of a window object have getter and setter methods. However, the value of
     * an Id is part of its primary key and must me immutable. Hence, setter method is private to prevent
     * client code from changing the value.
     *
     * @param id to set
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Instrument immutable id
     * @return id
     */
    public Long getId()
    {
        return this.id;
    }


    public Long getGroupingId()
    {
        return groupingId;
    }

    public void setGroupingId(Long groupingId)
    {
        this.groupingId = groupingId;
    }

    public Set<ManyToManyTargetConfigurationValue> getTargetValues()
    {
        return targetValues;
    }

    public void setTargetValues(Set<ManyToManyTargetConfigurationValue> targetValues)
    {
        this.targetValues = targetValues;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceValueTargetValueGrouping that = (SourceValueTargetValueGrouping) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (groupingId != null ? !groupingId.equals(that.groupingId) : that.groupingId != null) return false;
        return targetValues != null ? targetValues.equals(that.targetValues) : that.targetValues == null;

    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (groupingId != null ? groupingId.hashCode() : 0);
        result = 31 * result + (targetValues != null ? targetValues.hashCode() : 0);
        return result;
    }
}
