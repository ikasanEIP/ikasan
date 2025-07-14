package org.ikasan.module.builder;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AbstractTest {

    protected String loadDataFile(String fileName) throws IOException {
        return IOUtils.toString(loadDataFileStream(fileName), StandardCharsets.UTF_8);
    }

    protected InputStream loadDataFileStream(String fileName) {
        return getClass().getResourceAsStream(fileName);
    }
}
