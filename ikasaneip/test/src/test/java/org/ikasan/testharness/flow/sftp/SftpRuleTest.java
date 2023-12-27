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
package org.ikasan.testharness.flow.sftp;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.TestSocketUtils;

import java.io.File;
import java.io.InputStream;

/**
 * Functional test cases for SftpRule
 *
 * @author Ikasan Development Team
 */
public class SftpRuleTest
{
    @Test
    public void test_sftp_put_filename_with_string_content() throws Throwable
    {
        SftpRule sftp = new SftpRule("test", "test", "./target", TestSocketUtils.findAvailableTcpPort());
        sftp.start();
        sftp.putFile("testDownload.txt","testContent");
        InputStream inputStream = sftp.getFile("testDownload.txt");
        byte[] readin = new byte[11];
        inputStream.read(readin);
        String result = new String(readin);

        Assert.assertTrue("testContent".equals(result));
    }

    @Test
    public void test_sftp_put_filename_with_bytes_content() throws Throwable
    {
        SftpRule sftp = new SftpRule("test", "test", "./target", TestSocketUtils.findAvailableTcpPort());
        sftp.start();
        sftp.putFile("testDownload.txt","testContent".getBytes());
        InputStream inputStream = sftp.getFile("testDownload.txt");
        byte[] readin = new byte[11];
        inputStream.read(readin);
        String result = new String(readin);

        Assert.assertTrue("testContent".equals(result));
    }

    @Test
    public void test_sftp_put_file_content() throws Throwable
    {
        File file = new File("src/test/resources/testDownload.txt");
        SftpRule sftp = new SftpRule("test", "test", "./target", TestSocketUtils.findAvailableTcpPort());
        sftp.start();
        sftp.putFile(file);
        InputStream inputStream = sftp.getFile("testDownload.txt");
        byte[] readin = new byte[6];
        inputStream.read(readin);
        String result = new String(readin);

        Assert.assertTrue("line 1".equals(result));
    }

    @Test(expected = Exception.class)
    public void test_sftp_put_file_content_read_for_different_filename() throws Throwable
    {
        File file = new File("test/src/test/resources/testDownload.txt");
        SftpRule sftp = new SftpRule("test", "test", "./target", TestSocketUtils.findAvailableTcpPort());
        sftp.start();
        sftp.putFile(file);
        sftp.getFile("noFileExists.txt");
    }
}
