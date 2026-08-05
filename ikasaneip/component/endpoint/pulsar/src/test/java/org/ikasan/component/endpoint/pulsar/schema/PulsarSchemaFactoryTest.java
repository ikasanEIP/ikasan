package org.ikasan.component.endpoint.pulsar.schema;

import org.apache.pulsar.client.api.Schema;
import org.ikasan.component.endpoint.pulsar.consumer.configuration.PulsarConsumerConfiguration;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Comprehensive test suite for PulsarSchemaFactory
 *
 * @author Ikasan Development Team
 */
public class PulsarSchemaFactoryTest {

    @Test
    public void test_create_bytes_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("BYTES");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be BYTES", Schema.BYTES, schema);
    }

    @Test
    public void test_create_bytes_schema_default() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        // schemaType defaults to BYTES

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be BYTES by default", Schema.BYTES, schema);
    }

    @Test
    public void test_create_schema_null_config() {
        Schema<?> schema = PulsarSchemaFactory.createSchema(null);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should default to BYTES", Schema.BYTES, schema);
    }

    @Test
    public void test_create_string_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("STRING");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be STRING", Schema.STRING, schema);
    }

    @Test
    public void test_create_json_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("JSON");
        config.setSchemaMessageClassName("org.ikasan.component.endpoint.pulsar.consumer.TestMessage");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema info type should be JSON",
                     org.apache.pulsar.common.schema.SchemaType.JSON,
                     schema.getSchemaInfo().getType());
    }

    @Test(expected = PulsarSchemaCreationException.class)
    public void test_create_json_schema_without_class_name() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("JSON");
        // messageClassName not set

        PulsarSchemaFactory.createSchema(config);
    }

    @Test(expected = PulsarSchemaCreationException.class)
    public void test_create_json_schema_with_invalid_class_name() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("JSON");
        config.setSchemaMessageClassName("com.invalid.NonExistentClass");

        PulsarSchemaFactory.createSchema(config);
    }

    @Test
    public void test_create_avro_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("AVRO");
        config.setSchemaMessageClassName("org.ikasan.component.endpoint.pulsar.consumer.TestMessage");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema info type should be AVRO",
                     org.apache.pulsar.common.schema.SchemaType.AVRO,
                     schema.getSchemaInfo().getType());
    }

    @Test(expected = PulsarSchemaCreationException.class)
    public void test_create_avro_schema_without_class_name() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("AVRO");
        // messageClassName not set

        PulsarSchemaFactory.createSchema(config);
    }

    @Test
    public void test_create_auto_consume_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("AUTO_CONSUME");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
    }

    @Test
    public void test_create_int8_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("INT8");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be INT8", Schema.INT8, schema);
    }

    @Test
    public void test_create_int16_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("INT16");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be INT16", Schema.INT16, schema);
    }

    @Test
    public void test_create_int32_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("INT32");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be INT32", Schema.INT32, schema);
    }

    @Test
    public void test_create_int64_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("INT64");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be INT64", Schema.INT64, schema);
    }

    @Test
    public void test_create_bool_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("BOOL");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be BOOL", Schema.BOOL, schema);
    }

    @Test
    public void test_create_float_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("FLOAT");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be FLOAT", Schema.FLOAT, schema);
    }

    @Test
    public void test_create_double_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("DOUBLE");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be DOUBLE", Schema.DOUBLE, schema);
    }

    @Test
    public void test_create_date_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("DATE");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be DATE", Schema.DATE, schema);
    }

    @Test
    public void test_create_time_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("TIME");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be TIME", Schema.TIME, schema);
    }

    @Test
    public void test_create_timestamp_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("TIMESTAMP");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be TIMESTAMP", Schema.TIMESTAMP, schema);
    }

    @Test
    public void test_create_instant_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("INSTANT");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be INSTANT", Schema.INSTANT, schema);
    }

    @Test
    public void test_create_local_date_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("LOCAL_DATE");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be LOCAL_DATE", Schema.LOCAL_DATE, schema);
    }

    @Test
    public void test_create_local_time_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("LOCAL_TIME");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be LOCAL_TIME", Schema.LOCAL_TIME, schema);
    }

    @Test
    public void test_create_local_date_time_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("LOCAL_DATE_TIME");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be LOCAL_DATE_TIME", Schema.LOCAL_DATE_TIME, schema);
    }

    @Test
    public void test_create_key_value_schema() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("KEY_VALUE");
        config.setSchemaKeyType("STRING");
        config.setSchemaValueType("JSON");
        config.setSchemaValueClassName("org.ikasan.component.endpoint.pulsar.consumer.TestMessage");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema info type should be KEY_VALUE",
                     org.apache.pulsar.common.schema.SchemaType.KEY_VALUE,
                     schema.getSchemaInfo().getType());
    }

    @Test
    public void test_create_key_value_schema_with_separated_encoding() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("KEY_VALUE");
        config.setSchemaKeyType("STRING");
        config.setSchemaValueType("BYTES");
        config.setSchemaKeyValueEncodingType("SEPARATED");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema info type should be KEY_VALUE",
                     org.apache.pulsar.common.schema.SchemaType.KEY_VALUE,
                     schema.getSchemaInfo().getType());
    }

    @Test
    public void test_create_key_value_schema_with_inline_encoding() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("KEY_VALUE");
        config.setSchemaKeyType("STRING");
        config.setSchemaValueType("STRING");
        config.setSchemaKeyValueEncodingType("INLINE");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema info type should be KEY_VALUE",
                     org.apache.pulsar.common.schema.SchemaType.KEY_VALUE,
                     schema.getSchemaInfo().getType());
    }

    @Test(expected = PulsarSchemaCreationException.class)
    public void test_create_key_value_schema_without_key_type() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("KEY_VALUE");
        config.setSchemaValueType("STRING");
        // keySchemaType not set

        PulsarSchemaFactory.createSchema(config);
    }

    @Test(expected = PulsarSchemaCreationException.class)
    public void test_create_key_value_schema_without_value_type() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("KEY_VALUE");
        config.setSchemaKeyType("STRING");
        // valueSchemaType not set

        PulsarSchemaFactory.createSchema(config);
    }

    @Test
    public void test_create_schema_case_insensitive() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("string");  // lowercase

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should be STRING", Schema.STRING, schema);
    }

    @Test
    public void test_create_schema_unknown_type_defaults_to_bytes() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();
        config.setSchemaType("UNKNOWN_TYPE");

        Schema<?> schema = PulsarSchemaFactory.createSchema(config);

        assertNotNull("Schema should not be null", schema);
        assertEquals("Schema should default to BYTES for unknown type", Schema.BYTES, schema);
    }

    @Test
    public void test_schema_configuration_defaults() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();

        assertEquals("Default schema type should be BYTES", "BYTES", config.getSchemaType());
        assertEquals("Default key-value encoding should be INLINE", "INLINE", config.getSchemaKeyValueEncodingType());
        assertNull("Default message class name should be null", config.getSchemaMessageClassName());
        assertNull("Default key schema type should be null", config.getSchemaKeyType());
        assertNull("Default value schema type should be null", config.getSchemaValueType());
    }

    @Test
    public void test_schema_configuration_setters_and_getters() {
        PulsarConsumerConfiguration config = new PulsarConsumerConfiguration();

        config.setSchemaType("JSON");
        config.setSchemaMessageClassName("com.example.MyMessage");
        config.setSchemaAvroDefinition("{\"type\":\"record\",\"name\":\"test\"}");
        config.setSchemaKeyType("STRING");
        config.setSchemaValueType("AVRO");
        config.setSchemaKeyClassName("java.lang.String");
        config.setSchemaValueClassName("com.example.ValueClass");
        config.setSchemaKeyValueEncodingType("SEPARATED");
        config.setSchemaProperties(Map.of("property", "value"));

        assertEquals("JSON", config.getSchemaType());
        assertEquals("com.example.MyMessage", config.getSchemaMessageClassName());
        assertEquals("{\"type\":\"record\",\"name\":\"test\"}", config.getSchemaAvroDefinition());
        assertEquals("STRING", config.getSchemaKeyType());
        assertEquals("AVRO", config.getSchemaValueType());
        assertEquals("java.lang.String", config.getSchemaKeyClassName());
        assertEquals("com.example.ValueClass", config.getSchemaValueClassName());
        assertEquals("SEPARATED", config.getSchemaKeyValueEncodingType());
        assertEquals(Map.of("property", "value"), config.getSchemaProperties());
    }
}
