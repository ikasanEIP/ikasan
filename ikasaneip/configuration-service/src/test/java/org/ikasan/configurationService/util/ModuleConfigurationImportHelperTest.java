package org.ikasan.configurationService.util;

import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Ikasan Development Team on 25/12/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/configuration-service-conf.xml",
        "/hsqldb-datasource-conf.xml",
        "/substitute-components.xml"
})
public class ModuleConfigurationImportHelperTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    @Resource
    ModuleConfigurationImportHelper helper;

    @Test
    public void test()
    {

    }
}
