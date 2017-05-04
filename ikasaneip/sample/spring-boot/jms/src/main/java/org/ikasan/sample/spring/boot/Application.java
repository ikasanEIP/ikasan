package org.ikasan.sample.spring.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.util.SocketUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableAutoConfiguration

public class Application
{
    public static void main(String[] args) throws Exception
    {

        SpringApplication springContext =
                new SpringApplication(Application.class);
        int randomPort = SocketUtils.findAvailableTcpPort();
        Map<String, Object> map = new HashMap<>();
        map.put("SERVER_PORT", ""+randomPort);
        springContext.setDefaultProperties(map);

        springContext.addListeners(
                new ApplicationPidFileWriter());
        springContext.run(args);

        //ApplicationContext context = (ApplicationContext) springContext;
        //System.out.println("Let's inspect the beans provided by Spring Boot:");
        //String[] beanNames = context.getBeanDefinitionNames();
        //Arrays.sort(beanNames);
        //for (String beanName : beanNames)
       // {
        //    System.out.println(beanName);
        //}
    }
}