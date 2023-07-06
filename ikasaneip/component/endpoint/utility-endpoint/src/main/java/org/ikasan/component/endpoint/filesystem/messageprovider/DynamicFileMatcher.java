/*
 * $Id$
 * $URL$
 *
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.component.endpoint.filesystem.messageprovider;

import org.ikasan.spec.component.endpoint.EndpointListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * DynamicFileMatcher which extends FileMatcher.
 *
 */
public class DynamicFileMatcher extends FileMatcher {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicFileMatcher.class);

    public static final String FILE_NAME_PATTERN = "fileNamePattern";
    public static final String CORRELATING_IDENTIFIER = "correlatingIdentifier";
    public static final String REGEX = "regex:";

    /**
     * fileNamePattern that will be dynamically generated
     */
    private final String fileNamePattern;

    /**
     * endpoint listener to callback on event
     */
    private final EndpointListener<String, IOException> endpointListener;
    /**
     * spel expression can be null
     */
    private final String spelExpression;

    private String correlatingIdentifier;

    private String transientCorrelatingIdentifier = null;

    private PathMatcher matcher;

    /**
     * Constructor
     *
     * @param ignoreFileRenameWhilstScanning
     * @param parentPath
     * @param fileNamePattern
     * @param endpointListener
     */
    DynamicFileMatcher(boolean ignoreFileRenameWhilstScanning,
                       String parentPath,
                       String fileNamePattern,
                       int directoryDepth,
                       EndpointListener<String, IOException> endpointListener,
                       String spelExpression) {
        super(ignoreFileRenameWhilstScanning, parentPath, fileNamePattern, directoryDepth, endpointListener);

        this.fileNamePattern = fileNamePattern;
        this.endpointListener = endpointListener;

        this.spelExpression = spelExpression;
    }

    /**
     * Compares the regexp against the file/directory name
     *
     * @param path
     */
    @Override
    FileVisitResult match(Path path) {
        if (!endpointListener.isActive()) {
            return FileVisitResult.TERMINATE;
        }

        Path name = path.getFileName();

        if(this.transientCorrelatingIdentifier == null ||
            !this.transientCorrelatingIdentifier.equals(this.correlatingIdentifier)) {
            this.transientCorrelatingIdentifier = correlatingIdentifier;
            this.initialiseMatcher();
        }

        if (name != null && matcher != null && matcher.matches(name)) {
            this.endpointListener.onMessage(path.toString());
        }

        return FileVisitResult.CONTINUE;
    }

    private void initialiseMatcher() {
        String dynamicFilePattern = this.fileNamePattern;
        if (spelExpression != null) {
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            // NOTE: variable names can be null for spel expression evaluation
            // it is up to the spel expression to decide if it needs them or not
            // in this case fileNamePattern will almost always be needed if evaluating it
            evaluationContext.setVariable(FILE_NAME_PATTERN, this.fileNamePattern);
            evaluationContext.setVariable(CORRELATING_IDENTIFIER, this.correlatingIdentifier);

            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(this.spelExpression);

            dynamicFilePattern = exp.getValue(evaluationContext, String.class);
        }

        // create a new matcher every time as the fileNamePattern can change potentially every time
        LOG.info("Initialising path matcher with dynamicFilePattern[{}]", dynamicFilePattern);
        this.matcher = FileSystems.getDefault().getPathMatcher(REGEX + dynamicFilePattern);
    }

    /**
     * walk dir callback for files
     *
     * @param file
     * @param attrs
     * @return
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        return this.match(file);
    }

    /**
     * Walk dir callback for directories
     *
     * @param dir
     * @param attrs
     * @return
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        return this.match(dir);
    }

    public void setCorrelatingIdentifier(String correlatingIdentifier) {
        this.correlatingIdentifier = correlatingIdentifier;
    }
}
