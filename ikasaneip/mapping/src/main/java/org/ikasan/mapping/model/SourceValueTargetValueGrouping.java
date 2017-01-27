package org.ikasan.mapping.model;

/**
 * Created by stewmi on 25/01/2017.
 */
public class SourceValueTargetValueGrouping
{
    private SourceValueTargetValueGroupingPk id;
    private Long groupingId;
    private Long targetValueId;

    public SourceValueTargetValueGroupingPk getId()
    {
        return id;
    }

    public void setId(SourceValueTargetValueGroupingPk id)
    {
        this.id = id;
    }

    public Long getGroupingId()
    {
        return groupingId;
    }

    public void setGroupingId(Long groupingId)
    {
        this.groupingId = groupingId;
    }

    public Long getTargetValueId()
    {
        return targetValueId;
    }

    public void setTargetValueId(Long targetValueId)
    {
        this.targetValueId = targetValueId;
    }
}
