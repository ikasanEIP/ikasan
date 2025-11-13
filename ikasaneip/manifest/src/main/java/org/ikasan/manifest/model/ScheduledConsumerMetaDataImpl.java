package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.ScheduledConsumerMetaData;
import org.ikasan.spec.metadata.TypeParameter;

import java.util.List;
import java.util.Objects;

public class ScheduledConsumerMetaDataImpl implements ScheduledConsumerMetaData {

    private String name;
    private String flow;
    private String messageProviderClass;
    private List<TypeParameter> constructorArguments;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFlow() {
        return flow;
    }

    @Override
    public void setFlow(String flow) {
        this.flow = flow;
    }

    @Override
    public String getMessageProviderClass() {
        return messageProviderClass;
    }

    @Override
    public void setMessageProviderClass(String messageProviderClass) {
        this.messageProviderClass = messageProviderClass;
    }

    @Override
    public List<TypeParameter> getConstructorArguments() {
        return constructorArguments;
    }

    @Override
    public void setConstructorArguments(List<TypeParameter> constructorArguments) {
        this.constructorArguments = constructorArguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledConsumerMetaDataImpl that = (ScheduledConsumerMetaDataImpl) o;
        return Objects.equals(messageProviderClass, that.messageProviderClass)
            && Objects.equals(constructorArguments, that.constructorArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageProviderClass, constructorArguments);
    }
}
