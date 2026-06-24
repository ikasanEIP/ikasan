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

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.ikasan.rest.module.exception.MaxThreadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class MonitoringFileService {

    @Value("${sse.max.stream.threads:100}")
    private int maxStreamThreads;

    @Value("${sse.thread.wait.time:500}")
    private int streamThreadWaitTime;

    @Value("${sse.inactive.time.millis:300000}")
    private long inactiveTimeForFileInMillis;

    @Value("${sse.log.base.paths:.}")
    private String logBasePaths;

    private ExecutorService executorService;

    private List<Path> allowedBasePaths;

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(maxStreamThreads);
        allowedBasePaths = parseAndNormalizeBasePaths(logBasePaths);
    }

    public SseEmitter addMonitoringFileService(String fullFilePath) throws IOException {
        checkMaximumNumberOfThreads();
        String validatedPath = validateAndResolveFullFilePath(fullFilePath);
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        executorService.submit(new MonitoringFileServiceThread(validatedPath, sseEmitter, streamThreadWaitTime, inactiveTimeForFileInMillis));
        return sseEmitter;
    }

    /**
     * Checks if the current number of active threads in the thread pool executor
     * has reached the maximum allowable limit configured for streaming threads.
     * If the limit is exceeded, a {@link MaxThreadException} is thrown to indicate
     * that no more streaming threads can be created.
     *
     * @throws MaxThreadException if the number of active threads exceeds the configured maximum.
     */
    private void checkMaximumNumberOfThreads() throws MaxThreadException {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) executorService;
        if (tpe.getActiveCount() >= maxStreamThreads) {
            throw new MaxThreadException("Maximum number of log file streaming threads reached");
        }
    }

    /**
     * Parses a comma-separated list of base paths and normalizes them to absolute paths.
     * Each path in the list is converted to an absolute, normalized path for security validation.
     *
     * @param basePathsConfig the comma-separated string of base paths from configuration
     * @return a list of normalized absolute Path objects
     */
    private List<Path> parseAndNormalizeBasePaths(String basePathsConfig) {
        if (basePathsConfig == null || basePathsConfig.trim().isEmpty()) {
            return Arrays.asList(Paths.get(".").toAbsolutePath().normalize());
        }

        return Arrays.stream(basePathsConfig.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .map(pathStr -> Paths.get(pathStr).toAbsolutePath().normalize())
            .collect(Collectors.toList());
    }

    /**
     * Validates and resolves the provided file path against the configured list of base directories.
     * The method decodes the input file path, resolves it to an absolute path,
     * and ensures that the resolved path does not escape any of the configured base directories.
     *
     * @param fullFilePath the input file path to be validated and resolved.
     *                     This path is expected to be provided as a string,
     *                     which may be either absolute or relative.
     * @return the validated and resolved absolute file path as a string.
     * @throws IllegalArgumentException if the resolved file path escapes all configured base directories.
     */
    private String validateAndResolveFullFilePath(String fullFilePath) {
        String decodedPath = URLDecoder.decode(fullFilePath, StandardCharsets.UTF_8);
        Path requestedPath = Paths.get(decodedPath);

        // Normalize the requested path
        Path resolvedPath = requestedPath.isAbsolute()
            ? requestedPath.toAbsolutePath().normalize()
            : null;

        // Check if the path is within any of the allowed base paths
        for (Path baseDir : allowedBasePaths) {
            Path candidatePath;

            if(resolvedPath != null) {
                candidatePath = resolvedPath;
            }
            else {
                candidatePath = Path.of(".").toAbsolutePath().resolve(requestedPath).normalize();
            }

            if (candidatePath.startsWith(baseDir)) {
                return candidatePath.toString();
            }
        }

        throw new IllegalArgumentException("Invalid fullFilePath: path escapes all configured log base directories");
    }
}
