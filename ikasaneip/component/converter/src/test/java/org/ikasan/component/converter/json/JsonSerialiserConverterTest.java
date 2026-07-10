package org.ikasan.component.converter.json;

import net.sf.json.test.JSONAssert;
import org.junit.Before;
import org.junit.Test;
import tools.jackson.databind.json.JsonMapper;

public class JsonSerialiserConverterTest
{
    private static final String ID_FIELD = "id1";

    private static final int VALUE_FIELD = 1234;

    private JsonSerialiserConverter<TestPojo> uut;

    private TestPojo testPojo;

    private String serialisedPojo;

    @Before public void setup()
    {
        uut = new JsonSerialiserConverter<>();
        testPojo = new TestPojo();
        testPojo.id = ID_FIELD;
        testPojo.value = VALUE_FIELD;
        serialisedPojo = "{\"id\":\"" + ID_FIELD + "\",\"value\":" + VALUE_FIELD + "}";
    }

    @Test public void test_serialise_with_non_default_mapper()
    {
        uut = new JsonSerialiserConverter<>(JsonMapper.builder().build());
        String result = uut.convert(testPojo);
        JSONAssert.assertEquals(serialisedPojo, result);
    }

    @Test public void test_serialise()
    {
        String result = uut.convert(testPojo);
        JSONAssert.assertEquals(serialisedPojo, result);
    }

    public static class TestPojo
    {
        public String id;

        public Integer value;
    }
}