package org.ikasan.rest.module.sse;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class MonitoringFileServiceThreadTest {

    private SseEmitter sseEmitter = mock(SseEmitter.class);

    private final String sampleLogFileStr = "src/test/resources/data/log.sample";

    @Before
    public void setup() throws IOException {
        FileChannel.open(Paths.get(sampleLogFileStr), StandardOpenOption.WRITE).truncate(0).close();
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
    @Ignore
    public void test_monitoringService_monitorsForMessages_should_reset_atomic_count_if_file_deleted_and_send_new_messages() throws Exception {
        // create new file
        String pathname = "src/test/resources/data/log1.sample";
        File newFile = new File(pathname);
        FileUtils.write(newFile, "111", StandardCharsets.UTF_8);

        // start the thread
        MonitoringFileServiceThread monitoringFileService = new MonitoringFileServiceThread(pathname, sseEmitter, 100, 300000);
        monitoringFileService.start();

        // verify has sent message 1
        verifySseEmiterAndCounter(1, 3, monitoringFileService);

        // add another line
        FileUtils.writeLines(Paths.get(pathname).toFile(), List.of("222"), true);

        // verify has sent message 2
        verifySseEmiterAndCounter(2, 7, monitoringFileService);

        // recreate the file
        FileUtils.forceDelete(new File(pathname));
        newFile = new File(pathname);
        FileUtils.write(newFile, "", StandardCharsets.UTF_8);

        // wait until counter is reset to 0
        verifySseEmiterAndCounter(2, 0, monitoringFileService);

        // add another line
        FileUtils.writeLines(Paths.get(pathname).toFile(), List.of("333"), true);

        // verify has sent message 3
        verifySseEmiterAndCounter(3, 4, monitoringFileService);

        assertEquals(monitoringFileService.getState(), Thread.State.TIMED_WAITING);

        monitoringFileService.interrupt();

        // clean up
        FileUtils.forceDelete(new File(pathname));
    }

    @Test
    @Ignore
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
            .atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                verify(sseEmitter, times(seeEmitterCount)).send(any(SseEmitter.SseEventBuilder.class));
                assertEquals(fileMessageCounter, ((AtomicLong) ReflectionTestUtils.getField(service, "counter")).get());
            });
    }
}