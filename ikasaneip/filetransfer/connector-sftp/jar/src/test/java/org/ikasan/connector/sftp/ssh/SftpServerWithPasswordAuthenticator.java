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
package org.ikasan.connector.sftp.ssh;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.UserAuth;
import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.command.ScpCommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.sftp.SftpSubsystem;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A SFTP helper class used for unit testing sftp server.
 *
 */
public class SftpServerWithPasswordAuthenticator
{
    private SshServer sshd;

    public SftpServerWithPasswordAuthenticator(int port) {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider("hostkey.ser"));

        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<NamedFactory<UserAuth>>();
        userAuthFactories.add(new UserAuthPassword.Factory());

        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setPasswordAuthenticator(new PasswordAuthenticator()
        {
            public boolean authenticate(String username, String password, ServerSession session)
            {
                return true;
            }
        });

        sshd.setCommandFactory(new ScpCommandFactory());

        List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
        namedFactoryList.add(new SftpSubsystem.Factory());
        sshd.setSubsystemFactories(namedFactoryList);


        //FileKeyPairProvider fileKeyPairProvider = new FileKeyPairProvider(new String[]{HOST_KEY});
        //sshd.setKeyPairProvider(fileKeyPairProvider);
//        SftpSubsystem.Factory factory = new SftpSubsystem.Factory();
//        sshd.setSubsystemFactories(Arrays.<NamedFactory<Command>>asList(factory));
//        sshd.setCommandFactory(new ScpCommandFactory());
//        sshd.setShellFactory(new ProcessShellFactory());
//        sshd.setPasswordAuthenticator(PasswordAuthenticator());
    }

    private PasswordAuthenticator PasswordAuthenticator() {
        return new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String arg0, String arg1, ServerSession arg2) {
                return true;
            }};
    }

    public void start(){
        try {
            sshd.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        try {
            sshd.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();}
        sshd = null;
    }
}
