package org.ikasan.module.builder.service;

import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.builder.AbstractTest;
import org.ikasan.module.builder.model.component.Component;
import org.ikasan.module.builder.model.module.ModuleModel;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ModuleManifestMetaDataComponentModelAdapterTest extends AbstractTest {

    @Test
    public void test() throws IOException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaData.json");
        ModuleManifestMetaDataComponentModelAdapter adapter = new ModuleManifestMetaDataComponentModelAdapter();

        List<Component> components = adapter.adapt(ModuleManifestMetaDataHelper
            .deserialiseModuleManifest(moduleMetaData), "com.ikasan.sample.spring.boot", true);

        System.out.println(components);
    }
}
