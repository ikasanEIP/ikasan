package org.ikasan.ootb.scheduled.service;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MonitoringFileServiceTest {

    private final SseEmitter sseEmitter = mock(SseEmitter.class);

    private final String sampleLogFileStr = "src/test/resources/data/log.sample";

    @Before
    public void setup() throws IOException {
        FileChannel.open(Paths.get(sampleLogFileStr), StandardOpenOption.WRITE).truncate(0).close();
    }

    @Test
    public void test_monitoringService() throws IOException {

        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), Arrays.asList("111"), true);

        MonitoringFileService monitoringFileService = new MonitoringFileService(sampleLogFileStr, sseEmitter);

        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), Arrays.asList("222"), true);

        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
            .atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {

                verify(sseEmitter,times(2)).send(any(SseEmitter.SseEventBuilder.class));
            });
    }

    @Test
    public void test_monitoringService_no_call() throws IOException {

        MonitoringFileService monitoringFileService = new MonitoringFileService(sampleLogFileStr, sseEmitter);

        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
            .atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {

                verify(sseEmitter,times(0)).send(any(SseEmitter.SseEventBuilder.class));
            });
    }


}
