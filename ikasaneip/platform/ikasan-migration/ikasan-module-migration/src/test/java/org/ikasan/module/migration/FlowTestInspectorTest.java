package org.ikasan.module.migration;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlowTestInspectorTest {

    @TempDir
    Path tempDir;

    @Test
    public void test_against_real_project() throws IOException, XmlPullParserException {
//        FlowTestInspector inspector = new FlowTestInspector();
//        List<File> flowTests = inspector.findFlowTests(new File("/Users/mick/workspace/archetype/jms-demo"));
//        assertEquals(1, flowTests.size());
//        assertEquals("JmsSampleFlowTest.java", flowTests.get(0).getName());
//
//        TestClassEditor testClassEditor = new TestClassEditor("com.ikasan.sample.spring.boot", flowTests.get(0)
//        , new File("/Users/mick/workspace/archetype/moduleMetaData.json"), new File("/Users/mick/workspace/archetype/springContext.json"));
//        testClassEditor.addMetaDataGenerationMethod("metadata_extractor");
//        testClassEditor.addAutowiredApplicationContext(flowTests.get(0));
//        testClassEditor.addJsonConfigurationMetaDataExtractor(flowTests.get(0));
//        testClassEditor.addJsonModuleMetaDataProvider(flowTests.get(0));
//        PomEditor.addDependency(new File("/Users/mick/workspace/archetype/jms-demo/jar/pom.xml")
//            , "org.ikasan","ikasan-manifest", "4.1.1-SNAPSHOT");

        ModuleMigration migration = new ModuleMigration("/Users/mick/workspace/archetype/jms-demo"
            , "com.ikasan.sample.spring.boot", "/Users/mick/workspace/archetype"
            , "JmsSampleFlowTest");
        migration.migrate();
    }
}
