package org.ikasan.component.endpoint.bigqueue.serialiser;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class TestEvent {
    private String someValue1;
    private String someValue2;
    private List<TestParam> params;

    public TestEvent() {
    }

    public String getSomeValue1() {
        return someValue1;
    }

    public void setSomeValue1(String someValue1) {
        this.someValue1 = someValue1;
    }

    public String getSomeValue2() {
        return someValue2;
    }

    public void setSomeValue2(String someValue2) {
        this.someValue2 = someValue2;
    }

    public List<TestParam> getParams() {
        return params;
    }

    public void setParams(List<TestParam> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
