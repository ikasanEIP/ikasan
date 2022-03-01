/*
 * $Id$
 * $URL$
 *
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */

package org.ikasan.rest.module.sse;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class MonitoringFileServiceThread extends Thread {
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringFileServiceThread.class);
    private AtomicLong counter = new AtomicLong(0);

    private final SseEmitter sseEmitter;
    private final Path filePath;
    private File file;
    private final Path monitoringDirectory;
    private final WatchKey key;
    private final int streamThreadWaitTime;

    private long inactiveTimeForFileInMillis;
    private long lastTimeFileChanged;

    public MonitoringFileServiceThread(String fullFilePath, SseEmitter sseEmitter, int streamThreadWaitTime, long inactiveTimeForFileInMillis) throws IOException {
        this.streamThreadWaitTime = streamThreadWaitTime;
        this.sseEmitter = sseEmitter;
        this.inactiveTimeForFileInMillis = inactiveTimeForFileInMillis;

        String decodedPath = URLDecoder.decode(fullFilePath, StandardCharsets.UTF_8);

        this.file = new FileSystemResource(decodedPath).getFile();
        this.filePath = file.toPath();
        this.monitoringDirectory = file.getParentFile().toPath();

        WatchService watchService = FileSystems.getDefault().newWatchService();
        this.key = monitoringDirectory.register(watchService, ENTRY_MODIFY, ENTRY_CREATE);
        lastTimeFileChanged = System.currentTimeMillis();
    }

    @Override
    public void run() {
        sendAllMessagesForTheFirstTime();
        while (true) {
            monitorForMessages();
            sendDisconnectIfNoActivity();
        }
    }

    private void monitorForMessages() {
        try {
            Thread.sleep(streamThreadWaitTime);
            for (WatchEvent<?> event : key.pollEvents()) {
                Path changed = monitoringDirectory.resolve((Path) event.context());
                if (event.kind() == ENTRY_MODIFY && changed.equals(filePath)) {
                    sendMessage();
                } else if (event.kind() == ENTRY_CREATE && changed.equals(filePath)) {
                    counter = new AtomicLong(0);
                }
                // got some sort of poll event reset
                lastTimeFileChanged = System.currentTimeMillis();
            }

            boolean isKeyStillValid = key.reset();
            if (!isKeyStillValid) {
                LOG.error("Key is no longer valid: " + key);
                end(new IOException("Watch key is no longer valid"));
            }
        } catch (NoSuchFileException ns) {
            // do nothing as chances are the file is being rolled
            // the constructor will get an error if the file does not exist
        } catch (Exception ex) {
            end(ex);
        }
    }

    private void sendDisconnectIfNoActivity() {
        if (shouldDisconnect()) {
            sseEmitter.complete();
            this.interrupt();
            throw new ThreadDeath();
        }
    }

    private boolean shouldDisconnect() {
        return System.currentTimeMillis() > lastTimeFileChanged + inactiveTimeForFileInMillis;
    }

    private void sendAllMessagesForTheFirstTime() {
        try {
            sendMessage();
        } catch (IOException e) {
            end(e);
        }
    }

    private void sendMessage() throws IOException {
        RandomAccessFile radFile = new RandomAccessFile(file, "r");
        radFile.seek(counter.get());
        String line;
        while ((line = radFile.readLine()) != null) {
            try {
                sseEmitter.send(SseEmitter.event().data(line));
            } catch (IOException e) {
                end(e);
            }
        }

        counter.set(radFile.getFilePointer());
        radFile.close();
    }

    private void end(Exception e) {
        sseEmitter.completeWithError(e);
        this.interrupt();
        throw new ThreadDeath();
    }
}
