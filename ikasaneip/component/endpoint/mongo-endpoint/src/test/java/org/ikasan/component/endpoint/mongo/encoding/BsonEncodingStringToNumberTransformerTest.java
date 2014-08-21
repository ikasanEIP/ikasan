package org.ikasan.component.endpoint.mongo.encoding;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author edwaki
 *
 */
public class BsonEncodingStringToNumberTransformerTest
{
    private BsonEncodingStringToNumberTransformer bsonEncodingStringToNumberTransformer;

    @Before
    public void setup()
    {
        bsonEncodingStringToNumberTransformer = new BsonEncodingStringToNumberTransformer();
    }
    
    @Test
    public void testTransformPositiveInteger()
    {
        String testNumberStr = "31451";
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals("The test long number string should have been transformed to an integer", 31451, transformedNumber);
    }

    @Test
    public void testTransformNegativeInteger()
    {
        String testNumberStr = "-31451";
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals("The test long number string should have been transformed to an integer", -31451,
            transformedNumber);
    }

    @Test
    public void testTransformLongPositive()
    {
        String testNumberStr = (new Long(new Long(Integer.MAX_VALUE) + 1l)).toString();
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals("The test number string should have been transformed", Long.parseLong(testNumberStr),
            transformedNumber);
    }

    @Test
    public void testTransformLongNegative()
    {
        String testNumberStr = (new Long(new Long(Integer.MIN_VALUE) - 1l)).toString();
        Object transformedNumber = bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals("The test long number string should have been transformed", Long.parseLong(testNumberStr),
            transformedNumber);
    }

    @Test
    public void testTransformDouble()
    {
        String testNumberStr = "31451.45222";
        Double transformedNumber = (Double) bsonEncodingStringToNumberTransformer.transform(testNumberStr);
        assertEquals("The test double string should have been transformed", 31451.45222d,
            transformedNumber.doubleValue(), 0.000005d);
    }
}
