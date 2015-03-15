package org.ikasan.management.jmx.logging;


public interface Log4jConfiguratorMXBean {
    /**
     * list of all the logger names and their levels
     */
    String[] getLoggers();
 
    /**
     * Get the log level for a given logger
     */
    String getLogLevel(String logger);
 
    /**
     * Set the log level for a given logger
     */
    void setLogLevel(String logger, String level);
}