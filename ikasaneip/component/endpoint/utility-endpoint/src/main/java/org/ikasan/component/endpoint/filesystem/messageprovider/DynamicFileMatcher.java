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
 *
 */
public class DynamicFileMatcher extends FileMatcher {
    private static final Logger LOG = LoggerFactory.getLogger(DynamicFileMatcher.class);

    public static final String FILE_NAME_PATTERN = "fileNamePattern";
    public static final String FILE_PATH_PATTERN = "filePathPattern";
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

    private final String fileNameSpelExpression;

    private final String filePathSpelExpression;

    private String correlatingIdentifier;

    private String transientCorrelatingIdentifier = null;

    private PathMatcher matcher;


    /**
     * Constructor for DynamicFileMatcher class.
     *
     * @param ignoreFileRenameWhilstScanning whether to ignore file rename whilst scanning
     * @param parentPath the parent path to start scanning from
     * @param fileNamePattern the pattern to match against file names
     * @param directoryDepth the depth of directories to scan
     * @param endpointListener the listener for message and exception exchange
     * @param fileNameSpelExpression SpEL expression for dynamic file names
     * @param filePathSpelExpression SpEL expression for dynamic file paths
     * @param followSymbolicLinks whether to follow symbolic links
     */
    DynamicFileMatcher(boolean ignoreFileRenameWhilstScanning,
                       String parentPath,
                       String fileNamePattern,
                       int directoryDepth,
                       EndpointListener<String, IOException> endpointListener,
                       String fileNameSpelExpression,
                       String filePathSpelExpression,
                       boolean followSymbolicLinks) {
        super(ignoreFileRenameWhilstScanning, parentPath, fileNamePattern
            , directoryDepth, endpointListener, followSymbolicLinks);

        this.fileNamePattern = fileNamePattern;
        this.endpointListener = endpointListener;

        this.fileNameSpelExpression = fileNameSpelExpression;
        this.filePathSpelExpression = filePathSpelExpression;
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

    /**
     * Initialise the path matcher using the provided fileNameSpelExpression if available.
     * If fileNameSpelExpression is provided, evaluate it using the fileNamePattern and correlatingIdentifier variables.
     * The dynamicFilePattern is set based on the evaluation result.
     * Create a new matcher every time as the fileNamePattern can change potentially every time.
     * Logs the dynamicFilePattern being used for the path matcher initialization.
     */
    private void initialiseMatcher() {
        String dynamicFilePattern = this.fileNamePattern;
        if (fileNameSpelExpression != null) {
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            // NOTE: variable names can be null for spel expression evaluation
            // it is up to the spel expression to decide if it needs them or not
            // in this case fileNamePattern will almost always be needed if evaluating it
            evaluationContext.setVariable(FILE_NAME_PATTERN, this.fileNamePattern);
            evaluationContext.setVariable(CORRELATING_IDENTIFIER, this.correlatingIdentifier);

            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(this.fileNameSpelExpression);

            dynamicFilePattern = exp.getValue(evaluationContext, String.class);
        }

        // create a new matcher every time as the fileNamePattern can change potentially every time
        LOG.info("Initialising dynamic file name matcher with dynamicFilePattern[{}]", dynamicFilePattern);
        this.matcher = FileSystems.getDefault().getPathMatcher(REGEX + dynamicFilePattern);
    }


    /**
     * Initialises the dynamic filepath based on the provided SpEL expression.
     * If filePathSpelExpression is not null, evaluates it using the FILE_PATH_PATTERN and CORRELATING_IDENTIFIER variables.
     * The parentPath is updated with the evaluated result.
     */
    private void initialiseDynamicFilepath() {
        if (filePathSpelExpression != null) {
            StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
            // NOTE: variable names can be null for spel expression evaluation
            // it is up to the spel expression to decide if it needs them or not
            // in this case fileNamePattern will almost always be needed if evaluating it
            evaluationContext.setVariable(FILE_PATH_PATTERN, super.parentPath);
            evaluationContext.setVariable(CORRELATING_IDENTIFIER, this.correlatingIdentifier);

            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(this.filePathSpelExpression);

            super.parentPath = exp.getValue(evaluationContext, String.class);
        }
        LOG.info("Initialising dynamic file path matcher with dynamicFilePattern[{}]", super.parentPath);
    }

    @Override
    public void invoke() throws IOException {
        this.initialiseDynamicFilepath();
        super.invoke();
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
