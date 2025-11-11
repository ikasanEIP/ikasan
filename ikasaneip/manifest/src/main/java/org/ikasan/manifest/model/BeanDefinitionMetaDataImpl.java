package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.BeanDefinitionMetaData;

import java.util.Objects;

public class BeanDefinitionMetaDataImpl implements BeanDefinitionMetaData {
    private String beanName;
    private String type;
    private String beanClass;
    private String beanResource;

    @Override
    public String getBeanName() {
        return this.beanName;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getBeanClass() {
        return beanClass;
    }

    @Override
    public void setBeanClass(String beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public String getBeanResource() {
        return this.beanResource;
    }

    @Override
    public void setBeanResource(String beanResource) {
        this.beanResource = beanResource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeanDefinitionMetaDataImpl that = (BeanDefinitionMetaDataImpl) o;
        return Objects.equals(beanName, that.beanName) && Objects.equals(type, that.type) && Objects.equals(beanClass, that.beanClass) && Objects.equals(beanResource, that.beanResource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName, type, beanClass, beanResource);
    }
}
