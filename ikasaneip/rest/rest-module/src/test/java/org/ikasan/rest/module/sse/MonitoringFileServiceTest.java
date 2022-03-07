package org.ikasan.rest.module.sse;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.io.FileUtils;
import org.ikasan.rest.module.exception.MaxThreadException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class MonitoringFileServiceTest {

    private final String sampleLogFileStr = "target/tmp/data/log.sample";

    private MonitoringFileService service;

    @Before
    public void setup() throws IOException {
        FileUtils.write(new File(sampleLogFileStr), "", StandardCharsets.UTF_8);
        service = new MonitoringFileService();
        ReflectionTestUtils.setField(service, "maxStreamThreads", 1);
        ReflectionTestUtils.setField(service, "streamThreadWaitTime", 500);
        ReflectionTestUtils.setField(service, "inactiveTimeForFileInMillis", 300000);
        service.init();
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.forceDelete(new File(sampleLogFileStr));
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(service, "executorService");
        tpe.shutdownNow();
    }

    @Test
    public void shouldReturnEmitter() throws IOException {
        assertNotNull(service.addMonitoringFileService(sampleLogFileStr));
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(service, "executorService");
        assertEquals(tpe.getActiveCount(), 1);
    }

    @Test
    public void shouldThrowExceptionIfExceedsMaxStreamThreads() throws IOException {
        service.addMonitoringFileService(sampleLogFileStr);
        try {
            service.addMonitoringFileService(sampleLogFileStr);
            fail("should not get here");
        } catch (MaxThreadException e) {
            assertEquals("Maximum number of log file streaming threads reached", e.getLocalizedMessage());
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) ReflectionTestUtils.getField(service, "executorService");
            assertEquals(tpe.getActiveCount(), 1);
        }
    }
}