package com.ikasan.sample.spring.boot.builderpattern.components.router.configuration;

    import java.lang.Integer;
    import java.lang.Long;
    import java.lang.String;
    import java.util.ArrayList;

public class MySingleRecipientRouterConfiguration {
    private Integer intValue;
    private Long longValue;
    private String stringValue;
    private ArrayList<String> values;

    /**
    * Set the intValue configuration value.
    *
    * @param intValue the configuration value to set.
    */
    public Integer getIntValue() {
        return intValue;
    }

    /**
    * Get the intValue configuration value.
    *
    * @return intValue configuration value.
    */
    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }
    /**
    * Set the longValue configuration value.
    *
    * @param longValue the configuration value to set.
    */
    public Long getLongValue() {
        return longValue;
    }

    /**
    * Get the longValue configuration value.
    *
    * @return longValue configuration value.
    */
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }
    /**
    * Set the stringValue configuration value.
    *
    * @param stringValue the configuration value to set.
    */
    public String getStringValue() {
        return stringValue;
    }

    /**
    * Get the stringValue configuration value.
    *
    * @return stringValue configuration value.
    */
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }
    /**
    * Set the values configuration value.
    *
    * @param values the configuration value to set.
    */
    public ArrayList<String> getValues() {
        return values;
    }

    /**
    * Get the values configuration value.
    *
    * @return values configuration value.
    */
    public void setValues(ArrayList<String> values) {
        this.values = values;
    }
}
