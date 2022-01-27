package org.ikasan.job.orchestration.model.context;

import org.ikasan.spec.scheduled.context.model.And;
import org.ikasan.spec.scheduled.context.model.LogicalGrouping;
import org.ikasan.spec.scheduled.context.model.Not;
import org.ikasan.spec.scheduled.context.model.Or;

import java.util.List;

public class LogicalGroupingImpl implements LogicalGrouping {
    private LogicalGrouping logicalGrouping;
    private List<And> and;
    private List<Or> or;
    private List<Not> not;

    public LogicalGrouping getLogicalGrouping() {
        return logicalGrouping;
    }

    public void setLogicalGrouping(LogicalGrouping logicalGrouping) {
        this.logicalGrouping = logicalGrouping;
    }

    public List<And> getAnd() {
        return and;
    }

    public void setAnd(List<And> and) {
        this.and = and;
    }

    public List<Or> getOr() {
        return or;
    }

    public void setOr(List<Or> or) {
        this.or = or;
    }

    public List<Not> getNot() {
        return not;
    }

    public void setNot(List<Not> not) {
        this.not = not;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("LogicalGrouping{");
        sb.append("logicalGrouping=").append(logicalGrouping);
        sb.append(", and=").append(and);
        sb.append(", or=").append(or);
        sb.append(", not=").append(not);
        sb.append('}');
        return sb.toString();
    }
}
