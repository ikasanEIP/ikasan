/*
 * $Id: FileBasedPasswordHelper.java 36554 2014-05-15 12:17:16Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/Ikasan-0.8.4.x/connector-ftp/client/src/main/java/org/ikasan/endpoint/ftp/util/FileBasedPasswordHelper.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.endpoint.ftp.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author CMI2 Development Team
 *
 */
public class FileBasedPasswordHelper
{
    /**
     * 
     * @param filepath
     * @return
     * @throws IOException
     */
    public String getPasswordFromFile(String filepath) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String password = reader.readLine();

        if(password == null || password.length() == 0)
        {
            throw new IOException("FTP password cannot be null or empty!");
        }

        return password.trim();
    }
}
