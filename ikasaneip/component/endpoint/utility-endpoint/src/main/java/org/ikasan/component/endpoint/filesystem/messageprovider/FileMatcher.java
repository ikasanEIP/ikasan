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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

/**
 * FileMatcher which walks the directory tree and uses regular expressions to locate any matching file paths.
 *
 * @author mitcje
 */
public class FileMatcher extends SimpleFileVisitor<Path>
{
    /** parent path */
    private String parentPath;

    /** matcher instance */
    private final PathMatcher matcher;

    /** endpoint listener to callback on event */
    private EndpointListener<String, IOException> endpointListener;

    /** depth of the directory tree to walk */
    private int directoryDepth;

    /**
     * Constructor
     * @param parentPath
     * @param pattern
     * @param endpointListener
     */
    FileMatcher(String parentPath, String pattern, int directoryDepth, EndpointListener<String, IOException> endpointListener)
    {
        this.parentPath = parentPath;
        if(parentPath == null)
        {
            throw new IllegalArgumentException("parentPath cannot be 'null'");
        }

        matcher = FileSystems.getDefault().getPathMatcher("regex:" + pattern);
        this.endpointListener = endpointListener;
        this.directoryDepth = directoryDepth;
    }


    /**
     * Compares the regexp against the file/directory name
     * @param path
     */
    void match(Path path)
    {
        Path name = path.getFileName();

        if (name != null && matcher.matches(name))
        {
            this.endpointListener.onMessage(path.toString());
        }
    }

    /**
     * walk dir callback for files
     * @param file
     * @param attrs
     * @return
     */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
    {
        match(file);
        return FileVisitResult.CONTINUE;
    }

    /**
     * Walk dir callback for directories
     * @param dir
     * @param attrs
     * @return
     */
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
    {
        match(dir);
        return FileVisitResult.CONTINUE;
    }

    /**
     * Walk dir callback on failure encountered with a directory or file.
     * @param file
     * @param exception
     * @return
     */
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exception)
    {
        this.endpointListener.onException(exception);
        return FileVisitResult.TERMINATE;
    }

    /**
     * Invoke the walk dir for this fileMatcher instance.
     * @throws IOException
     */
    public void invoke() throws IOException
    {
        Files.walkFileTree(Paths.get(parentPath), EnumSet.noneOf(FileVisitOption.class), directoryDepth, this);
    }
}
