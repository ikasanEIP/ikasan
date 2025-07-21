package org.ikasan.module.builder.service;

import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.builder.AbstractTest;
import org.ikasan.module.builder.model.ModuleModel;
import org.junit.Test;

import java.io.IOException;

public class ModuleMetaDataAdapterTest extends AbstractTest {

    @Test
    public void test() throws IOException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaData.json");
        ModuleMetaDataAdapter adapter = new ModuleMetaDataAdapter();

        ModuleModel model = adapter.adapt(ModuleManifestMetaDataHelper.deserialiseModuleManifest(moduleMetaData), "test");

        System.out.println(model);
    }
}
