package org.ikasan.ootb.scheduler.agent.module.component.broker;

import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessExecutionBrokerTest {

    @Test
    void get_Command_line_args() {
        ProcessExecutionBroker broker = new ProcessExecutionBroker("hostname");
        String cmd = "source $HOME/.bash_profile;\necho \"some_cmd(\\\"code = 'abc'\\\");\" | do_some_something - | blah.sh -\t\t\nblah1.sh -c some_param -i\n";
        String[] commandLineArgs = broker.getCommandLineArgs(cmd);
        if (SystemUtils.OS_NAME.contains("Windows")) {
            assertEquals("cmd.exe", commandLineArgs[0]);
            assertEquals("/c", commandLineArgs[1]);
        } else {
            // unix flavour
            assertEquals("/bin/bash", commandLineArgs[0]);
            assertEquals("-c", commandLineArgs[1]);
        }

        assertEquals(cmd, commandLineArgs[2]);
    }
}
