package org.ikasan.ootb.scheduled.processtracker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CommandProcessorTest {
    @Test
    void succesfully_identifies_correct_command_processor() {
        String[] commands = new String[] { "KSH" };
        CommandProcessor actual = CommandProcessor.getCommandProcessor(commands);
        assertThat(actual.toString()).isEqualTo(CommandProcessor.UNIX_KSH.toString());

        String[] commandsLower = new String[] { "ksh" };
        actual = CommandProcessor.getCommandProcessor(commandsLower);
        assertThat(actual.toString()).isEqualTo(CommandProcessor.UNIX_KSH.toString());

    }

    @Test
    void attempts_to_guesss_command_processor_when_no_details_passed() {
        String[] empty = new String[] { };

        CommandProcessor actual = CommandProcessor.getCommandProcessor(null);
        assertNotNull(actual.toString());
        assertNotEquals(actual.toString(), CommandProcessor.UNKNOWN.toString());

        actual = CommandProcessor.getCommandProcessor(empty);
        assertNotNull(actual.toString());
        assertNotEquals(actual.toString(), CommandProcessor.UNKNOWN.toString());
    }
}