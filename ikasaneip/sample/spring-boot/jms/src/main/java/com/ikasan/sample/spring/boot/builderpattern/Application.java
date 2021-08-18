package com.ikasan.sample.spring.boot.builderpattern;

import com.ulisesbocchio.jasyptspringboot.environment.StandardEncryptableEnvironment;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication public class Application
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder()
            .environment(new StandardEncryptableEnvironment())
            .sources(Application.class).run(args);
    }
}