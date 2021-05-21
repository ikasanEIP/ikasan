package org.ikasan.component.factory.spring;

public class CustomConverterConfiguration  {

    private boolean upperCase;

    private String prependText;

    private String appendText;

    public String getPrependText() {
        return prependText;
    }

    public void setPrependText(String prependText) {
        this.prependText = prependText;
    }

    public String getAppendText() {
        return appendText;
    }

    public void setAppendText(String appendText) {
        this.appendText = appendText;
    }

    public boolean isUpperCase() {
        return upperCase;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }
}
