package org.ikasan.module.migration;

import org.codejive.properties.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesMigrator {

    private static Logger logger = LoggerFactory.getLogger(PropertiesMigrator.class);

    public static void comparePropertiesFiles(String originalPropertiesFilePath
        , String generatedPropertiesFilePath) throws IOException {
        Properties prop1 = loadProperties(originalPropertiesFilePath);
        Properties prop2 = loadProperties(generatedPropertiesFilePath);

        logger.info("--- Comparing " + originalPropertiesFilePath + " and " + generatedPropertiesFilePath + " ---");

        // Use TreeSet for sorted keys for cleaner output
        Set<String> keys1 = new TreeSet<>(prop1.stringPropertyNames());
        Set<String> keys2 = new TreeSet<>(prop2.stringPropertyNames());

        // 1. Check for keys in File 1 missing in File 2
        Set<String> missingInFile2 = new TreeSet<>(keys1);
        missingInFile2.removeAll(keys2);
        if (!missingInFile2.isEmpty()) {
            logger.info("Keys present in File 1 but missing in File 2:");
            missingInFile2.forEach(f -> {
                logger.info(f);
                prop2.putCommented(f, prop1.getProperty(f), "property update from source");
            });
        }

        // 2. Check for keys in File 2 missing in File 1
        Set<String> missingInFile1 = new TreeSet<>(keys2);
        missingInFile1.removeAll(keys1);
        if (!missingInFile1.isEmpty()) {
            logger.info("Keys present in File 2 but missing in File 1:");
            missingInFile1.forEach(f -> logger.info(f));
        }

        // 3. Check for differing values in common keys
        Set<String> commonKeys = new TreeSet<>(keys1);
        commonKeys.retainAll(keys2);
        logger.info("Keys with different values:");
        boolean differencesFound = false;
        for (String key : commonKeys) {
            String value1 = prop1.getProperty(key);
            String value2 = prop2.getProperty(key);
            if (!value1.equals(value2)) {
                logger.info("Key: " + key);
                logger.info("  File 1 Value: " + value1);
                logger.info("  File 2 Value: " + value2);
//                prop2.put(key, value1);
                differencesFound = true;
            }
        }
        if (!differencesFound) {
            logger.info("None");
        }

        writeProperties(prop2, generatedPropertiesFilePath);
    }

    private static Properties loadProperties(String filePath) throws IOException {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(filePath)) {
            prop.load(input); // The Properties class can load data from an InputStream
        }
        return prop;
    }

    private static void writeProperties(Properties properties, String filePath) throws IOException {
        try (FileWriter output = new FileWriter(filePath)) {
            // The store method will use the overridden keys() method
            properties.store(output, "Example of Ordered Properties");
        } catch (IOException e) {
            throw e;
        }
    }
}
