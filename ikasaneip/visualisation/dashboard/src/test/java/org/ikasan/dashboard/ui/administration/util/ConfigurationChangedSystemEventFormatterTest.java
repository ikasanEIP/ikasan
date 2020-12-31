package org.ikasan.dashboard.ui.administration.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ConfigurationChangedSystemEventFormatterTest {

    public static final String SYSTEM_EVENT = "Configuration Updated OldConfig [{\"configurationId\":\"test-tradeTradeConsumerFlowexceptionBroker\"," +
        "\"description\":null,\"parameters\":[{\"id\":19,\"name\":\"shouldThrowExclusionException\",\"value\":false,\"description\":null},{\"id\":20,\"name\":" +
        "\"shouldThrowRecoveryException\",\"value\":false,\"description\":null},{\"id\":21,\"name\":\"shouldThrowStoppedInErrorException\",\"value\":false," +
        "\"description\":null},{\"id\":22,\"name\":\"testLong\",\"value\":7,\"description\":null}]}] NewConfig [{\"configurationId\":" +
        "\"test-tradeTradeConsumerFlowexceptionBroker\",\"description\":null,\"parameters\":[{\"id\":19,\"name\":\"shouldThrowExclusionException\"," +
        "\"value\":false,\"description\":null},{\"id\":20,\"name\":\"shouldThrowRecoveryException\",\"value\":false,\"description\":null},{\"id\":21,\"name\":" +
        "\"shouldThrowStoppedInErrorException\",\"value\":true,\"description\":null},{\"id\":22,\"name\":\"testLong\",\"value\":7,\"description\":null}]}]";

    public static final String RESULT = "Configuration Updated.\n" +
        "Old Configuration [\n" +
        "{\n" +
        "  \"configurationId\" : \"test-tradeTradeConsumerFlowexceptionBroker\",\n" +
        "  \"description\" : null,\n" +
        "  \"parameters\" : [ {\n" +
        "    \"id\" : 19,\n" +
        "    \"name\" : \"shouldThrowExclusionException\",\n" +
        "    \"value\" : false,\n" +
        "    \"description\" : null\n" +
        "  }, {\n" +
        "    \"id\" : 20,\n" +
        "    \"name\" : \"shouldThrowRecoveryException\",\n" +
        "    \"value\" : false,\n" +
        "    \"description\" : null\n" +
        "  }, {\n" +
        "    \"id\" : 21,\n" +
        "    \"name\" : \"shouldThrowStoppedInErrorException\",\n" +
        "    \"value\" : false,\n" +
        "    \"description\" : null\n" +
        "  }, {\n" +
        "    \"id\" : 22,\n" +
        "    \"name\" : \"testLong\",\n" +
        "    \"value\" : 7,\n" +
        "    \"description\" : null\n" +
        "  } ]\n" +
        "}\n" +
        "]\n" +
        "New Configuration [\n" +
        "{\n" +
        "  \"configurationId\" : \"test-tradeTradeConsumerFlowexceptionBroker\",\n" +
        "  \"description\" : null,\n" +
        "  \"parameters\" : [ {\n" +
        "    \"id\" : 19,\n" +
        "    \"name\" : \"shouldThrowExclusionException\",\n" +
        "    \"value\" : false,\n" +
        "    \"description\" : null\n" +
        "  }, {\n" +
        "    \"id\" : 20,\n" +
        "    \"name\" : \"shouldThrowRecoveryException\",\n" +
        "    \"value\" : false,\n" +
        "    \"description\" : null\n" +
        "  }, {\n" +
        "    \"id\" : 21,\n" +
        "    \"name\" : \"shouldThrowStoppedInErrorException\",\n" +
        "    \"value\" : true,\n" +
        "    \"description\" : null\n" +
        "  }, {\n" +
        "    \"id\" : 22,\n" +
        "    \"name\" : \"testLong\",\n" +
        "    \"value\" : 7,\n" +
        "    \"description\" : null\n" +
        "  } ]\n" +
        "}\n" +
        "]\n" +
        "Difference [\n" +
        "[ {\n" +
        "  \"op\" : \"replace\",\n" +
        "  \"path\" : \"/parameters/2/value\",\n" +
        "  \"value\" : true\n" +
        "} ]\n" +
        "]";
    
    @Test
    public void test_format_success() throws JsonProcessingException {
        Assertions.assertEquals(RESULT.replace("\n", "").trim(),
            ConfigurationChangedSystemEventFormatter.format(SYSTEM_EVENT).replace("\r", "").replace("\n", "").trim());
    }
}
