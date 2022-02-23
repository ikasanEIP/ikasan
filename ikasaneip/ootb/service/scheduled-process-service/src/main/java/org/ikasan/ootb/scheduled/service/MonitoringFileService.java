package org.ikasan.ootb.scheduled.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class MonitoringFileService {
    private static final Logger log = LoggerFactory.getLogger(MonitoringFileService.class);
    private final AtomicBoolean listen = new AtomicBoolean(false);
    private final AtomicLong COUNTER = new AtomicLong(0);
    private final AtomicBoolean firstTouch = new AtomicBoolean(true);

    private final WatchKey key;
    private final Path monitoringDirectory;
    private final Path file;
    private final SseEmitter sseEmitter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MonitoringFileService(String fullFilePath,
                                 SseEmitter sseEmitter) throws IOException {

        String decodedPath = URLDecoder.decode( fullFilePath, "UTF-8" );
        this.file = new FileSystemResource(decodedPath).getFile().toPath();
        this.monitoringDirectory = new FileSystemResource(decodedPath).getFile().getParentFile().toPath();
        this.sseEmitter = sseEmitter;
        final WatchService ws = FileSystems.getDefault().newWatchService();

        key = monitoringDirectory.register(ws, ENTRY_MODIFY);

        executorService.submit(() -> monitor());
    }

    private void sendMessage() throws IOException {
        Files.lines(file)
                .skip(COUNTER.get())
                .forEach(line ->
                        {
                            try {
                                COUNTER.incrementAndGet();
                                sseEmitter.send(SseEmitter.event()
                                        //.id(String.valueOf(COUNTER.incrementAndGet()))
                                        .data(line)
                                );
                            } catch (IOException e) {
                                listen.set(false);
                            }
                        }
                );
    }

    void monitor() {

        listen.set(true);

        while (listen.get()) {
            try {

                Thread.sleep(100);
                // first run
                if (firstTouch.get()) {
                    sendMessage();
                    firstTouch.set(false);
                }

                for (final WatchEvent<?> event : key.pollEvents()) {
                    final Path changed = monitoringDirectory.resolve((Path) event.context());

                    if (event.kind() == ENTRY_MODIFY && changed.equals(file)) {
                        sendMessage();
                    }
                }

                boolean isKeyStillValid = key.reset();
                if (!isKeyStillValid) {
                    log.trace("monitor - key is no longer valid: " + key);
                    listen.set(false);
                }
            } catch (Exception ex) {
                listen.set(false);
            }
        }
    }
}
