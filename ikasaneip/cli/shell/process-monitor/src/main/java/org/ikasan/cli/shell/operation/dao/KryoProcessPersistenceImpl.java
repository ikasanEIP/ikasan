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
package org.ikasan.cli.shell.operation.dao;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.ikasan.cli.shell.operation.model.IkasanProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Serialiser for IkasanProcess.
 *
 * @author Ikasan Development Team
 */
public class KryoProcessPersistenceImpl implements ProcessPersistenceDao
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(KryoProcessPersistenceImpl.class);

    /** persistence directory */
    String persistenceDir;

    File persistenceDirFile;

    /**
     * Thread local instance of Kyro instance.
     */
    private static final ThreadLocal<Kryo> kryoThreadLocal = new ThreadLocal<Kryo>()
    {
        @Override
        protected Kryo initialValue()
        {
            Kryo kryo = new Kryo();
            kryo.register(org.ikasan.cli.shell.operation.model.IkasanProcess.class);
            kryo.register(org.ikasan.cli.shell.operation.model.ProcessType.class);
            return kryo;
        }
    };

    /**
     * Constructor
     * @param persistenceDir
     */
    public KryoProcessPersistenceImpl(String persistenceDir)
    {
        this.persistenceDir = persistenceDir;
        if(persistenceDir == null)
        {
            throw new IllegalArgumentException("persistence directory cannot be 'null");
        }

        this.persistenceDirFile = new File(persistenceDir);
        if(!persistenceDirFile.exists())
        {
            persistenceDirFile.mkdirs();
        }
    }

    @Override
    public void save(IkasanProcess ikasanProcess)
    {
        Kryo kryo = kryoThreadLocal.get();
        String path = getPidFQN(ikasanProcess.getType(), ikasanProcess.getName());

        try
        {
            Output output = new Output(new FileOutputStream(path) );
            kryo.writeClassAndObject(output, ikasanProcess);
            output.close(); // flush should be called within the close() method
        }
        catch(FileNotFoundException e)
        {
            throw new RuntimeException("Failed to save the IkasanProcess", e);
        }
    }

    @Override
    public IkasanProcess find(String type, String name)
    {
        Kryo kryo = kryoThreadLocal.get();
        String path = getPidFQN(type, name);
        try (Input input = new Input(new FileInputStream(path)))
        {
            return (IkasanProcess)kryo.readClassAndObject(input);
        }
        catch(FileNotFoundException e)
        {
            logger.debug("File [" + path + "] not found", e);
            return null;
        }
        catch(IndexOutOfBoundsException ioobe) {
            logger.error("An attempt to read the pid file has failed, if running new jars against old instance, this can be ignored");
            return null;
        }
    }

    @Override
    public void delete(String type, String name)
    {
        String path = getPidFQN(type, name);
        try
        {
            Files.delete(Path.of(path));
        }
        catch(IOException e)
        {
            logger.warn("Failed to delete [" + path + "] file may be missing or some other IO issue" + e.getMessage());
        }
    }

    protected String getPidFQN(String type, String name)
    {
        return persistenceDir + FileSystems.getDefault().getSeparator() + type + "_" + name;
    }

    protected String getPidBaseDir()
    {
        return persistenceDir + FileSystems.getDefault().getSeparator();
    }

}