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
package org.ikasan.cli.shell.migration.dao;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ikasan.cli.shell.migration.model.IkasanMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Serialiser for IkasanProcess.
 *
 * @author Ikasan Development Team
 */
public class MigrationPersistenceDaoImpl implements MigrationPersistenceDao
{
    /** logger instance */
    private static Logger logger = LoggerFactory.getLogger(MigrationPersistenceDaoImpl.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    /** persistence directory */
    private String persistenceDir;

    private File persistenceDirFile;


    /**
     * Constructor
     * @param persistenceDir
     */
    public MigrationPersistenceDaoImpl(String persistenceDir) {
        this.persistenceDir = persistenceDir;
        if(persistenceDir == null) {
            throw new IllegalArgumentException("persistence directory cannot be 'null");
        }

        this.persistenceDirFile = new File(persistenceDir);
        if(!persistenceDirFile.exists()) {
            persistenceDirFile.mkdirs();
            File directorySafeGuardFile = new File(this.persistenceDir + "/DO_NOT_DELETE_ANY_FILES_IN_THIS_DIRECTORY");
            if(!directorySafeGuardFile.exists()) {
                try {
                    directorySafeGuardFile.createNewFile();
                }
                catch (IOException e) {
                    logger.warn("An error has occurred creating the directory safe guard file!", e);
                }
            }
        }
    }

    @Override
    public void save(IkasanMigration ikasanMigration)
    {
        String path = getMigrationManifestFilePath(ikasanMigration.getType()
            , ikasanMigration.getSourceVersion(), ikasanMigration.getTargetVersion());

        try(Output output = new Output(new FileOutputStream(path))) {
            objectMapper.writeValue(output, ikasanMigration);
        }
        catch(IOException e) {
            throw new RuntimeException("Failed to save the IkasanProcess", e);
        }
    }

    @Override
    public IkasanMigration find(String type, String sourceVersion, String targetVersion) {
        String path = getMigrationManifestFilePath(type, sourceVersion, targetVersion);
        try (Input input = new Input(new FileInputStream(path))) {
            return this.objectMapper.readValue(input, IkasanMigration.class);
        }
        catch(IOException e) {
            logger.debug("File [" + path + "] not found", e);
            return null;
        }
    }

    @Override
    public void delete(String type, String sourceVersion, String targetVersion) {
        String path = getMigrationManifestFilePath(type, sourceVersion, targetVersion);
        try {
            Files.delete(Path.of(path));
        }
        catch(IOException e) {
            logger.warn("Failed to delete [" + path + "] file may be missing or some other IO issue" + e.getMessage());
        }
    }

    protected String getMigrationManifestFilePath(String type, String sourceVersion, String targetVersion) {
        return persistenceDir + FileSystems.getDefault().getSeparator() + type + "_" + sourceVersion + "_" + targetVersion;
    }

}