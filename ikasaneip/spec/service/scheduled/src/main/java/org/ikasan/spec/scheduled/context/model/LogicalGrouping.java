package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;
import java.util.List;

public interface LogicalGrouping extends Serializable {

    public LogicalGrouping getLogicalGrouping();

    public void setLogicalGrouping(LogicalGrouping logicalGrouping);

    public List<And> getAnd();

    public void setAnd(List<And> and);

    public List<Or> getOr();

    public void setOr(List<Or> or);

    public List<Not> getNot();

    public void setNot(List<Not> not);

}
