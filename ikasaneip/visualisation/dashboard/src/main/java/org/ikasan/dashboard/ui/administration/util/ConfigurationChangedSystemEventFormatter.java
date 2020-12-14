package org.ikasan.dashboard.ui.administration.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;

public class ConfigurationChangedSystemEventFormatter {

    public static String format(String systemEventDetails) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        if(systemEventDetails.startsWith("Configuration Deleted")) {
            String json1 = systemEventDetails.substring(systemEventDetails.indexOf("OldConfig [") + "OldConfig [".length(), systemEventDetails.lastIndexOf("]"));

            JsonNode beforeNode = objectMapper.readTree(json1);
            return "Configuration Deleted\r\n"+objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(beforeNode);
        }
        else {
            String json1 = systemEventDetails.substring(systemEventDetails.indexOf("OldConfig [") + "OldConfig [".length(), systemEventDetails.indexOf("] NewConfig"));
            String json2 = systemEventDetails.substring(systemEventDetails.indexOf("NewConfig [") + "NewConfig [".length()
                , systemEventDetails.length() - 1);


            JsonNode beforeNode = objectMapper.readTree(json1);
            JsonNode afterNode = objectMapper.readTree(json2);
            JsonNode patchNode = JsonDiff.asJson(beforeNode, afterNode);

            StringBuilder result = new StringBuilder("Configuration Updated.\r\nOld Configuration [\r\n");
            result.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(beforeNode));
            result.append("\r\n]\r\nNew Configuration [\r\n");
            result.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(afterNode));
            result.append("\r\n]\r\nDifference [\r\n");
            result.append(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(patchNode));
            result.append("\r\n]");

            return result.toString();
        }
    }
}
