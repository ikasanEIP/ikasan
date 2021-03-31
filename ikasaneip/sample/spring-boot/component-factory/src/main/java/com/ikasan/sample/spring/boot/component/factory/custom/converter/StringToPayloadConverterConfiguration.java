package com.ikasan.sample.spring.boot.component.factory.custom.converter;

public class StringToPayloadConverterConfiguration {
    private String fileNamePrefix = new String();

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }
}
