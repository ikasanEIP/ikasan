package org.ikasan.component.validator.xml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Created by Ikasan Development Team on 03/08/2016.
 */
class ClasspathSchemaResolverTest
{

    @Test
    void testResolveSchema() throws Exception
    {
        ClasspathSchemaResolver resolver = new ClasspathSchemaResolver("xsd/book.xsd");

        assertTrue(resolver.getSchemaLocation().endsWith("/target/test-classes/xsd/book.xsd"));
    }

    @Test
    void testResolveSchema_not_on_classpath() throws Exception
    {
        assertThrows(RuntimeException.class, () -> {
            ClasspathSchemaResolver resolver = new ClasspathSchemaResolver("xsd/not_on_classpath.xsd");
        });
    }
}
