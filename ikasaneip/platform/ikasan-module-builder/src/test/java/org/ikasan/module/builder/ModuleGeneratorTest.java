package org.ikasan.module.builder;

import freemarker.template.TemplateException;
import org.ikasan.manifest.ModuleManifestMetaDataHelper;
import org.ikasan.module.migration.util.maven.MavenProjectBuilder;
import org.ikasan.module.migration.util.maven.file.ModuleFileManager;
import org.ikasan.spec.metadata.ModuleManifestMetaData;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ModuleGeneratorTest extends AbstractTest {

    @Test
    public void test_module_generation() throws IOException, TemplateException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaData.json");
        ModuleManifestMetaData root = ModuleManifestMetaDataHelper.deserialiseModuleManifest(moduleMetaData);
        File rootDir = new File("target/"+root.getModuleMetaData().getName());
        rootDir.mkdirs();

        ModuleFileManager moduleFileManager = new ModuleFileManager(rootDir);
        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleFileManager
            , "com.ikasan.sample.spring.boot", "org.ikasan");
        moduleGenerator.generate(root);

        MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder(System.getenv("M2_HOME"));
        Assert.assertTrue(mavenProjectBuilder.build(rootDir, "spotless:apply"));
        Assert.assertTrue(mavenProjectBuilder.build(rootDir, "clean install"));
    }

    @Test
    public void test_module_generation_complex() throws IOException, TemplateException {
        String moduleMetaData = this.loadDataFile("/data/moduleMetaDataComplex.json");
        ModuleManifestMetaData root = ModuleManifestMetaDataHelper.deserialiseModuleManifest(moduleMetaData);
        File rootDir = new File("target/"+root.getModuleMetaData().getName());
        rootDir.mkdirs();

        ModuleFileManager moduleFileManager = new ModuleFileManager(rootDir);
        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleFileManager
            , "com.acme.esb.sales.dumbStreamCms.operation", "com.acme.esb");
        moduleGenerator.generate(root);

        MavenProjectBuilder mavenProjectBuilder = new MavenProjectBuilder(System.getenv("M2_HOME"));
        Assert.assertTrue(mavenProjectBuilder.build(rootDir, "spotless:apply"));
        Assert.assertTrue(mavenProjectBuilder.build(rootDir, "clean install"));
    }
}
