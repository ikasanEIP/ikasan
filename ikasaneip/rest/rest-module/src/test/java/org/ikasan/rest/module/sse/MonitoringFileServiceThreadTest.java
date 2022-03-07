package org.ikasan.rest.module.sse;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class MonitoringFileServiceThreadTest {

    private final SseEmitter sseEmitter = mock(SseEmitter.class);

    private final String sampleLogFileStr = "target/tmp/data/log.sample";

    @Before
    public void setup() throws IOException {
        FileUtils.write(new File(sampleLogFileStr), "", StandardCharsets.UTF_8);
    }

    @After
    public void tearDown() throws IOException {
        FileUtils.forceDelete(new File(sampleLogFileStr));
    }

    @Test
    public void test_monitoringService_monitorsForMessages_disconnects_timeout() throws Exception {
        MonitoringFileServiceThread monitoringFileService = new MonitoringFileServiceThread(sampleLogFileStr, sseEmitter, 1, 10);

        monitoringFileService.start();

        Thread.sleep(1000);

        verify(sseEmitter, times(1)).complete();
        assertEquals(monitoringFileService.getState(), Thread.State.TERMINATED);
    }

    @Test
    public void test_monitoringService_monitorsForMessages_should_reset_atomic_count_if_file_deleted_and_send_new_messages() throws Exception {

        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("111"), true);
        MonitoringFileServiceThread monitoringFileService = new MonitoringFileServiceThread(sampleLogFileStr, sseEmitter, 100, 300000);
        monitoringFileService.start();

        // verify has sent message 1
        verifySseEmiterAndCounter(1, 4, monitoringFileService);

        // add another line
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("222"), true);

        // verify has sent message 2
        verifySseEmiterAndCounter(2, 8, monitoringFileService);

        // recreate the file
        FileUtils.forceDelete(new File(sampleLogFileStr));
        // this sleep is so we can run the tests on macs - takes a while for the delete to propagate events
        Thread.sleep(15000);

        FileUtils.write(new File(sampleLogFileStr), "", StandardCharsets.UTF_8);
        // wait until counter is reset to 0
        verifySseEmiterAndCounter(2, 0, monitoringFileService);

        // add another line
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("333"), true);

        // verify has sent message 3
        verifySseEmiterAndCounter(3, 4, monitoringFileService);

        assertEquals(monitoringFileService.getState(), Thread.State.TIMED_WAITING);

        monitoringFileService.interrupt();
    }

    @Test
    public void test_monitoringService_monitors_for_new_messages() throws IOException {

        MonitoringFileServiceThread monitoringFileService = new MonitoringFileServiceThread(sampleLogFileStr, sseEmitter, 100, 300000);

        monitoringFileService.start();

        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("111"), true);
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("222"), true);

        verifySseEmiterAndCounter(2, 8, monitoringFileService);

        assertEquals(monitoringFileService.getState(), Thread.State.TIMED_WAITING);
        monitoringFileService.interrupt();
    }

    @Test
    public void test_monitoringService_sends_all_messages_when_started() throws IOException {
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("111"), true);
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("222"), true);

        MonitoringFileServiceThread monitoringFileService = new MonitoringFileServiceThread(sampleLogFileStr, sseEmitter, 100, 300000);

        monitoringFileService.start();

        verifySseEmiterAndCounter(2, 8, monitoringFileService);

        assertEquals(monitoringFileService.getState(), Thread.State.TIMED_WAITING);
        monitoringFileService.interrupt();
    }

    @Test
    public void test_monitoringService_no_messages_to_send() throws IOException {
        MonitoringFileServiceThread monitoringFileService = new MonitoringFileServiceThread(sampleLogFileStr, sseEmitter, 100, 300000);

        monitoringFileService.start();

        verifySseEmiterAndCounter(0, 0, monitoringFileService);
        assertEquals(monitoringFileService.getState(), Thread.State.TIMED_WAITING);
        monitoringFileService.interrupt();
    }

    private void verifySseEmiterAndCounter(int seeEmitterCount, int fileMessageCounter, Thread service) {
        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
            .atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
                verify(sseEmitter, times(seeEmitterCount)).send(any(SseEmitter.SseEventBuilder.class));
                long counter = ((AtomicLong) ReflectionTestUtils.getField(service, "counter")).get();
                assertEquals(fileMessageCounter, counter);
            });
    }
}