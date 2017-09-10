package org.ikasan.management.jmx.logging;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class Log4jConfigurator implements Log4jConfiguratorMXBean {
    @Override
    public String[] getLoggers() {
        
        List<String> list = new ArrayList<String>();
        
        for (Enumeration e = LogManager.getCurrentLoggers();
             e.hasMoreElements(); ) {
 
            Logger log = (Logger) e.nextElement();
            if (log.getLevel() != null) {
                list.add(log.getName() + " = " + log.getLevel().toString());
            }
        }
        return list.toArray(new String[list.size()]);
    }
 
    @Override
    public String getLogLevel(String logger) {
        String level = "unavailable";
        
        if (StringUtils.isNotBlank(logger)) {
            Logger log = Logger.getLogger(logger);
 
            if (log != null) {
                level = log.getLevel().toString();
            }
        }
        return level;
    }
    @Override
    public void setLogLevel(String logger, String level) {
        if (StringUtils.isNotBlank(logger)  &&  StringUtils.isNotBlank(level)) {
            Logger log = Logger.getLogger(logger);
            if (log != null) {
                log.setLevel(Level.toLevel(level.toUpperCase()));
            }
        }
    }
 
}
