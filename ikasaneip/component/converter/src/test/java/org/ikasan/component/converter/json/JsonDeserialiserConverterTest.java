package org.ikasan.component.converter.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.spec.component.transformation.TransformationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonDeserialiserConverterTest
{
    private static final String ID_FIELD = "id1";

    private static final int VALUE_FIELD = 1234;

    private JsonDeserialiserConverter<TestPojo> uut;

    private String serialisedPojo;

    @BeforeEach
    void setup()
    {
        uut = new JsonDeserialiserConverter<>(TestPojo.class);
        serialisedPojo = "{\"id\":\"" + ID_FIELD + "\",\"value\":" + VALUE_FIELD + "}";
    }

    @Test
    void test_deserialise_with_non_default_mapper()
    {
        uut = new JsonDeserialiserConverter<>(TestPojo.class, new ObjectMapper());
        TestPojo result = uut.convert(serialisedPojo);
        assertThat(result.id).isEqualTo(ID_FIELD);
        assertThat(result.value).isEqualTo(VALUE_FIELD);
    }

    @Test
    void test_deserialise()
    {
        TestPojo result = uut.convert(serialisedPojo);
        assertThat(result.id).isEqualTo(ID_FIELD);
        assertThat(result.value).isEqualTo(VALUE_FIELD);
    }

    @Test
    void test_deserialise_invalid_json_string()
    {
        assertThrows(TransformationException.class, () -> {
            uut.convert("{\"invalid\"");
        });
    }

    public static class TestPojo
    {
        public String id;

        public Integer value;
    }
}