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

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.kex.BuiltinDHFactories;
import org.apache.sshd.common.keyprovider.FileKeyPairProvider;
import org.apache.sshd.common.random.JceRandomFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.UserAuth;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.config.keys.AuthorizedKeysAuthenticator;
import org.apache.sshd.server.kex.DHGServer;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * A SFTP helper class used for unit testing sftp server.
 */
public class SftpServerWithPublickeyAuthenticator
{

    private SshServer sshd;

    public SftpServerWithPublickeyAuthenticator(int port)
    {
        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(port);

        sshd.setKeyPairProvider(new FileKeyPairProvider(Paths.get("src/test/resources/auth/server/id_rsa_test")));

        sshd.setKeyExchangeFactories(singletonList(DHGServer.newFactory(BuiltinDHFactories.dhg1)));
        sshd.setRandomFactory(new JceRandomFactory());

        List<NamedFactory<UserAuth>> userAuthFactories = new ArrayList<>();
        userAuthFactories.add(new UserAuthPublicKeyFactory());
        sshd.setUserAuthFactories(userAuthFactories);

        sshd.setPublickeyAuthenticator(new AuthorizedKeysAuthenticator(Paths.get("src/test/resources/auth/server/authorized_keys_test")));

        sshd.setCommandFactory(new ScpCommandFactory());

        List<NamedFactory<Command>> namedFactoryList = new ArrayList<>();
        namedFactoryList.add(new SftpSubsystemFactory());
        sshd.setSubsystemFactories(namedFactoryList);
    }

    public void start()
    {
        try {
            sshd.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        try {
            sshd.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sshd = null;
    }
}
