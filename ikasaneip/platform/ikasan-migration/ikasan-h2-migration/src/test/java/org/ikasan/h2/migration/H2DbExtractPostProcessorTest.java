package org.ikasan.h2.migration;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static org.junit.Assert.assertTrue;

public class H2DbExtractPostProcessorTest {
    @Test
    public void test_post_process_success() throws IOException {

        H2DbExtractPostProcessor postProcessor
            = new H2DbExtractPostProcessor();
        postProcessor.filterInsertStatements(ResourceUtils.getFile("classpath:migration/script.sql"),
            ResourceUtils.getFile("./target/post-processed.sql"));


        Reader reader1 = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:migration/expected.sql")));
        Reader reader2 = new BufferedReader(new FileReader(ResourceUtils.getFile("./target/post-processed.sql")));

        assertTrue("The post processed and expected files differ!", IOUtils.contentEqualsIgnoreEOL(reader1, reader2));
    }
}
