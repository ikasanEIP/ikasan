package org.ikasan.component.endpoint.mongo.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * 
 * @author edwaki
 *
 */
class BsonEncodingStringToNumberTransformerTest
{
    private BsonEncodingStringToNumberTransformer bsonEncodingStringToNumberTransformer;

    @BeforeEach
    void setup()
    {
        bsonEncodingStringToNumberTransformer = new BsonEncodingStringToNumberTransformer();
    }

    @Test
    void testTransformPositiveInteger()
    {
        String testNumberStr = "31451";
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals(31451, transformedNumber, "The test long number string should have been transformed to an integer");
    }

    @Test
    void testTransformNegativeInteger()
    {
        String testNumberStr = "-31451";
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals(-31451,
            transformedNumber,
            "The test long number string should have been transformed to an integer");
    }

    @Test
    void testTransformLongPositive()
    {
        String testNumberStr = (Long.valueOf(Long.valueOf(Integer.MAX_VALUE) + 1l)).toString();
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals(Long.parseLong(testNumberStr),
            transformedNumber,
            "The test number string should have been transformed");
    }

    @Test
    void testTransformLongNegative()
    {
        String testNumberStr = (Long.valueOf(Long.valueOf(Integer.MIN_VALUE) - 1l)).toString();
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals(Long.parseLong(testNumberStr),
            transformedNumber,
            "The test long number string should have been transformed");
    }

    @Test
    void testTransformDouble()
    {
        String testNumberStr = "31451.45222";
        Double transformedNumber = (Double) bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals(31451.45222d,
            transformedNumber.doubleValue(), 0.000005d, "The test double string should have been transformed");
    }
}
