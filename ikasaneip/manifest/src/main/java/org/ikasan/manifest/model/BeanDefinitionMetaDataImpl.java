package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.BeanDefinitionMetaData;

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
}
