package org.ikasan.component.factory.spring;

import org.junit.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { IkasanComponentFactory.class,
    CustomConverterComponentFactory.class, MultipleFactoryConverterFactoryOne.class,
    MultipleFactoryConverterFactoryTwo.class }) public class IkasanComponentFactoryTest
{
    @Resource private IkasanComponentFactory ikasanComponentFactory;

    @Test public void test()
    {
        CustomConverter customConverter = ikasanComponentFactory
            .create("customConverter", "custom.converter", "custom.converter.factory",
                CustomConverter.class);
        assertNotNull(customConverter);
        String configuredResourceId = customConverter.getConfiguredResourceId();
        String flowName = customConverter.getFlowName(); // check factory config picked up
        CustomConverterConfiguration configuration = customConverter.getConfiguration();
        assertEquals("custom-converter-flow-one", flowName);
        assertEquals("component-factory-module-customConverter", configuredResourceId);
        assertEquals("before", configuration.getPrependText());
        assertEquals("after", configuration.getAppendText());
        assertEquals(true, configuration.isUpperCase());
    }

    @Test public void testWithoutFactory()
    {
        IkasanComponentFactoryException thrownException = assertThrows(IkasanComponentFactoryException.class, () -> {
            ConverterWithoutFactory customConverter = ikasanComponentFactory
                .create("customConverter", "custom.converter", "custom.converter.factory",
                    ConverterWithoutFactory.class);
        });
        assertEquals("Found no component factory for component class "
            + "org.ikasan.component.factory.spring.ConverterWithoutFactory", thrownException.getMessage());
    }

    @Test
    public void testMultipleFactoriesForComponent(){
        IkasanComponentFactoryException thrownException = assertThrows(IkasanComponentFactoryException.class, () -> {
            MultipleFactoryConverter multipleFactoryConverter = ikasanComponentFactory
                .create("multipleFactoryConverter", "multiple.converter",
                    "multiple.converter.factory",
                    MultipleFactoryConverter.class);
        });
        assertEquals( "Found 2 candidate factories for component class "
            + "org.ikasan.component.factory.spring.MultipleFactoryConverter please ensure there is only one defined",
            thrownException.getMessage());
    }
}