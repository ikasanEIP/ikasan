package org.ikasan.module.builder;

import freemarker.template.TemplateException;
import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.migration.util.maven.MavenProjectBuilder;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.spec.metadata.ModuleManifestMetaData;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ModuleGeneratorTest extends AbstractTest {

    @Test
    public void test_module_generation() throws IOException, TemplateException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaData.json");
        ModuleManifestMetaData root = ModuleManifestMetaDataHelper.deserialiseModuleManifest(moduleMetaData);
        File rootDir = new File(root.getModuleMetaData().getName());
        rootDir.mkdirs();

        ModuleFileManager moduleFileManager = new ModuleFileManager(rootDir);
        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleFileManager, "com.ikasan.sample.spring.boot");
        moduleGenerator.generate(root);

        MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder(System.getenv("M2_HOME"));
        mavenProjectBuilder.build(rootDir, "spotless:apply");
        mavenProjectBuilder.build(rootDir, "clean install");
    }
}
