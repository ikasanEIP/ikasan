package org.ikasan.h2.migration;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class H2DbExtractPostProcessorTest {
    @Test
    public void test_post_process_sucess() throws IOException {

        H2DbExtractPostProcessor postProcessor
            = new H2DbExtractPostProcessor();
        postProcessor.filterInsertStatements(ResourceUtils.getFile("classpath:migration/script.sql"),
            ResourceUtils.getFile("./target/post-processed.sql"));

        assertEquals("The files differ!",
            FileUtils.readFileToString(ResourceUtils.getFile("classpath:migration/expected.sql"), "utf-8").strip(),
            FileUtils.readFileToString(ResourceUtils.getFile("./target/post-processed.sql"), "utf-8").strip());
    }
}
