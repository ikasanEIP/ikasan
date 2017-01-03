package org.ikasan.component.validator.xml;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Ikasan Development Team on 03/08/2016.
 */
public class ClasspathSchemaResolverTest
{

    @Test
    public void testResolveSchema() throws Exception
    {
        ClasspathSchemaResolver resolver = new ClasspathSchemaResolver("xsd/book.xsd");

        Assert.assertTrue(resolver.getSchemaLocation().endsWith("/target/test-classes/xsd/book.xsd"));
    }

    @Test (expected = RuntimeException.class)
    public void testResolveSchema_not_on_classpath() throws Exception
    {
        ClasspathSchemaResolver resolver = new ClasspathSchemaResolver("xsd/not_on_classpath.xsd");
    }
}
