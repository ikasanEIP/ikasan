package org.ikasan.module.builder.service;

import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.builder.AbstractTest;
import org.ikasan.module.builder.model.configuration.ComponentConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ModuleManifestMetaDataConfigurationModelAdapterTest extends AbstractTest {

    @Test
    public void test() throws IOException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaData.json");
        ModuleManifestMetaDataConfigurationModelAdapter adapter
            = new ModuleManifestMetaDataConfigurationModelAdapter();

        List<ComponentConfiguration> componentConfigurations = adapter.adapt(ModuleManifestMetaDataHelper
            .deserialiseModuleManifest(moduleMetaData), "com.ikasan.sample.spring.boot");

        System.out.println(componentConfigurations);
    }
}
