package org.ikasan.management.jmx.logging;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/jmx-log4j-conf.xml", "/jmx-log4j-test-conf.xml" } )
public class Log4jConfiguratorTest
{
    
    @Resource
    private Log4jConfiguratorMXBean log4jConfiguratorClient;
    
    
    @Test
    public void testSetLogLevel()
    {
        log4jConfiguratorClient.setLogLevel("newLogger", "DEBUG");
        assertEquals(1, log4jConfiguratorClient.getLoggers().length);
        assertEquals("DEBUG", log4jConfiguratorClient.getLogLevel("newLogger"));
        log4jConfiguratorClient.setLogLevel("newLogger", "");
        assertEquals(1, log4jConfiguratorClient.getLoggers().length);
        log4jConfiguratorClient.setLogLevel("", "notblank");
        assertEquals(1, log4jConfiguratorClient.getLoggers().length);        
    }
    
    @Test 
    public void getBlankLogLevel() {
        assertEquals("unavailable", log4jConfiguratorClient.getLogLevel(null));
    }
    
    
}
