package org.ikasan.module.builder;

import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.IOException;

public class ModuleGeneratorTest extends AbstractTest {

    @Test
    public void test_module_generation() throws IOException, TemplateException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaData.json");

        ModuleGenerator moduleGenerator = new ModuleGenerator();
        moduleGenerator.generate(moduleMetaData);
    }
}
