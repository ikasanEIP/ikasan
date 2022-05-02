package org.ikasan.ootb.scheduler.agent.module.component.cli;


import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandLineArgsConfig {

    @Value( "${module.broker.cli.args:#{null}}")
    private List<String> commandLineArgs;

    @Bean
    public CommandLinesArgConverter commandLinesArgConverter() {
        return new CommandLinesArgConverter(commandLineArgs);
    }
 }
