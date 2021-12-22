package org.ikasan.spec.scheduled.context.model;

import java.util.List;

public interface LogicalGrouping {

    public LogicalGrouping getLogicalGrouping();

    public void setLogicalGrouping(LogicalGrouping logicalGrouping);

    public List<And> getAnd();

    public void setAnd(List<And> and);

    public List<Or> getOr();

    public void setOr(List<Or> or);

    public List<Not> getNot();

    public void setNot(List<Not> not);

}
