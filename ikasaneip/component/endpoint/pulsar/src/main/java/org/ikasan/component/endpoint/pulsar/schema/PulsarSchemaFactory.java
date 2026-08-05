package org.ikasan.component.endpoint.pulsar.schema;

import org.apache.pulsar.client.api.Schema;
import org.ikasan.component.endpoint.pulsar.consumer.configuration.PulsarConsumerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for creating Pulsar Schema instances based on configuration.
 * This factory is shared between consumers and producers to ensure consistent schema handling.
 *
 * @author Ikasan Development Team
 */
public class PulsarSchemaFactory {

    private static final Logger logger = LoggerFactory.getLogger(PulsarSchemaFactory.class);

    /**
     * Create a Pulsar Schema based on the provided configuration
     *
     * @param config Consumer configuration containing schema settings
     * @return Pulsar Schema instance
     */
    public static Schema<?> createSchema(PulsarConsumerConfiguration config) {
        if (config == null) {
            logger.info("No configuration provided, using default BYTES schema");
            return Schema.BYTES;
        }

        String schemaType = config.getSchemaType();
        if (schemaType == null || schemaType.isEmpty()) {
            logger.info("No schema type specified, using default BYTES schema");
            return Schema.BYTES;
        }

        try {
            switch (schemaType.toUpperCase()) {
                case "BYTES":
                    logger.debug("Creating BYTES schema");
                    return Schema.BYTES;

                case "STRING":
                    logger.debug("Creating STRING schema");
                    return Schema.STRING;

                case "JSON":
                    return createJsonSchema(config);

                case "AVRO":
                    return createAvroSchema(config);

                case "PROTOBUF":
                    return createProtobufSchema(config);

                case "PROTOBUF_NATIVE":
                    return createProtobufNativeSchema(config);

                case "KEY_VALUE":
                    return createKeyValueSchema(config);

                case "AUTO_CONSUME":
                    logger.debug("Creating AUTO_CONSUME schema");
                    return Schema.AUTO_CONSUME();

                case "AUTO_PRODUCE_BYTES":
                    logger.debug("Creating AUTO_PRODUCE_BYTES schema");
                    return Schema.AUTO_PRODUCE_BYTES();

                case "INT8":
                    logger.debug("Creating INT8 schema");
                    return Schema.INT8;

                case "INT16":
                    logger.debug("Creating INT16 schema");
                    return Schema.INT16;

                case "INT32":
                    logger.debug("Creating INT32 schema");
                    return Schema.INT32;

                case "INT64":
                    logger.debug("Creating INT64 schema");
                    return Schema.INT64;

                case "BOOL":
                    logger.debug("Creating BOOL schema");
                    return Schema.BOOL;

                case "FLOAT":
                    logger.debug("Creating FLOAT schema");
                    return Schema.FLOAT;

                case "DOUBLE":
                    logger.debug("Creating DOUBLE schema");
                    return Schema.DOUBLE;

                case "DATE":
                    logger.debug("Creating DATE schema");
                    return Schema.DATE;

                case "TIME":
                    logger.debug("Creating TIME schema");
                    return Schema.TIME;

                case "TIMESTAMP":
                    logger.debug("Creating TIMESTAMP schema");
                    return Schema.TIMESTAMP;

                case "INSTANT":
                    logger.debug("Creating INSTANT schema");
                    return Schema.INSTANT;

                case "LOCAL_DATE":
                    logger.debug("Creating LOCAL_DATE schema");
                    return Schema.LOCAL_DATE;

                case "LOCAL_TIME":
                    logger.debug("Creating LOCAL_TIME schema");
                    return Schema.LOCAL_TIME;

                case "LOCAL_DATE_TIME":
                    logger.debug("Creating LOCAL_DATE_TIME schema");
                    return Schema.LOCAL_DATE_TIME;

                default:
                    logger.warn("Unknown schema type: {}, using BYTES schema", schemaType);
                    return Schema.BYTES;
            }
        } catch (Exception e) {
            String message = "Failed to create schema of type: " + schemaType + ", falling back to BYTES schema";
            logger.error(message, e);
            throw new PulsarSchemaCreationException(message, e);
        }
    }

    /**
     * Create JSON schema from configuration
     */
    @SuppressWarnings("unchecked")
    private static Schema<?> createJsonSchema(PulsarConsumerConfiguration config) {
        String className = config.getSchemaMessageClassName();
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("schemaMessageClassName must be specified for JSON schema");
        }

        try {
            Class<?> messageClass = Class.forName(className);
            logger.debug("Creating JSON schema for class: {}", className);
            return Schema.JSON(messageClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className, e);
        }
    }

    /**
     * Create AVRO schema from configuration
     */
    @SuppressWarnings("unchecked")
    private static Schema<?> createAvroSchema(PulsarConsumerConfiguration config) {
        String className = config.getSchemaMessageClassName();
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("schemaMessageClassName must be specified for AVRO schema");
        }

        try {
            Class<?> messageClass = Class.forName(className);
            logger.debug("Creating AVRO schema for class: {}", className);
            return Schema.AVRO(messageClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className, e);
        }
    }

    /**
     * Create PROTOBUF schema from configuration
     */
    @SuppressWarnings("unchecked")
    private static Schema<?> createProtobufSchema(PulsarConsumerConfiguration config) {
        String className = config.getSchemaMessageClassName();
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("schemaMessageClassName must be specified for PROTOBUF schema");
        }

        try {
            Class messageClass = Class.forName(className);
            logger.debug("Creating PROTOBUF schema for class: {}", className);
            return Schema.PROTOBUF(messageClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className, e);
        }
    }

    /**
     * Create PROTOBUF_NATIVE schema from configuration
     */
    @SuppressWarnings("unchecked")
    private static Schema<?> createProtobufNativeSchema(PulsarConsumerConfiguration config) {
        String className = config.getSchemaMessageClassName();
        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("schemaMessageClassName must be specified for PROTOBUF_NATIVE schema");
        }

        try {
            Class messageClass = Class.forName(className);
            logger.debug("Creating PROTOBUF_NATIVE schema for class: {}", className);
            return Schema.PROTOBUF_NATIVE(messageClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class not found: " + className, e);
        }
    }

    /**
     * Create KEY_VALUE schema from configuration
     */
    @SuppressWarnings("unchecked")
    private static Schema<?> createKeyValueSchema(PulsarConsumerConfiguration config) {
        String keySchemaType = config.getSchemaKeyType();
        String valueSchemaType = config.getSchemaValueType();

        if (keySchemaType == null || keySchemaType.isEmpty()) {
            throw new IllegalArgumentException("schemaKeyType must be specified for KEY_VALUE schema");
        }
        if (valueSchemaType == null || valueSchemaType.isEmpty()) {
            throw new IllegalArgumentException("schemaValueType must be specified for KEY_VALUE schema");
        }

        // Create temporary configuration for key schema
        PulsarConsumerConfiguration keyConfig = new PulsarConsumerConfiguration();
        keyConfig.setSchemaType(keySchemaType);
        keyConfig.setSchemaMessageClassName(config.getSchemaKeyClassName());

        // Create temporary configuration for value schema
        PulsarConsumerConfiguration valueConfig = new PulsarConsumerConfiguration();
        valueConfig.setSchemaType(valueSchemaType);
        valueConfig.setSchemaMessageClassName(config.getSchemaValueClassName());

        Schema<?> keySchema = createSchema(keyConfig);
        Schema<?> valueSchema = createSchema(valueConfig);

        logger.debug("Creating KEY_VALUE schema with key type: {} and value type: {}",
                     keySchemaType, valueSchemaType);

        // Determine encoding type
        org.apache.pulsar.common.schema.KeyValueEncodingType encodingType;
        if ("SEPARATED".equalsIgnoreCase(config.getSchemaKeyValueEncodingType())) {
            encodingType = org.apache.pulsar.common.schema.KeyValueEncodingType.SEPARATED;
        } else {
            encodingType = org.apache.pulsar.common.schema.KeyValueEncodingType.INLINE;
        }

        return Schema.KeyValue(keySchema, valueSchema, encodingType);
    }
}
