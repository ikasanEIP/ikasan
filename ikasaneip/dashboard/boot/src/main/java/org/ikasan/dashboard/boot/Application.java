package org.ikasan.dashboard.boot;

import org.ikasan.dashboard.discovery.DiscoveryJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableAutoConfiguration
public class Application
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(DiscoveryJob.class);

    public static void main(String[] args) throws Exception
    {
        SpringApplication.run(Application.class, args);
        logger.info("Ikasan Dashboard successfully bootstrapped.");
    }
}