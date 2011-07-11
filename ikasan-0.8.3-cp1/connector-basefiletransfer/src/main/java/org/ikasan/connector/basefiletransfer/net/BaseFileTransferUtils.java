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
package org.ikasan.connector.basefiletransfer.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for File Transfers
 * 
 * @author Ikasan Development Team
 */
public class BaseFileTransferUtils
{
    /** UTF-8 encoding */
    public final static String UTF8 = "UTF-8";
    
    /**
     * Return true if the filename matches the filter
     * 
     * @param patternString
     * @param filename
     * @return true if the file matches the filter else false
     */
    public boolean fileFilter(String patternString, String filename)
    {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(filename);
        return matcher.matches();
    }

    /**
     * Return a filtered List of ClientListEntries
     * 
     * @param files
     * @param filters
     * @return List of ClientListEntries
     */
    public ArrayList<ClientListEntry> filterFiles(ArrayList<ClientListEntry> files, ArrayList<ClientFilter> filters)
    {
        ArrayList<ClientListEntry> filteredFiles = new ArrayList<ClientListEntry>();
        for(ClientFilter filter: filters)
        {
            for(ClientListEntry file: files)
            {
                if (filter.match(file))
                {
                    filteredFiles.add(file);
                }
            }
        }
        return filteredFiles; 
    }
    
    /**
     * By default case sensitive and based on US ASCII characters
     * 
     * @param patternString
     * @param fileList
     * @return List of ClientListEntries
     */
    public ArrayList<ClientListEntry> matchFiles(String patternString,
            ArrayList<ClientListEntry> fileList)
    {
        ArrayList<ClientListEntry> filteredList = new ArrayList<ClientListEntry>();
        Pattern pattern = Pattern.compile(patternString);
        for (ClientListEntry fileEntry : fileList)
        {
            File file = new File(fileEntry.getUri().getPath());
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.matches())
            {
                filteredList.add(fileEntry);
            }
        }
        return filteredList;
    }

    /**
     * Encodes the bytes of a Unicode String to a given Charset characters. 
     * Expected Charset values can be:
     * 
     * <ul>
     *  <li>US-ASCII</li>
     *  <li>ISO-8859-1</li>
     *  <li>UTF-8</li>
     *  <li>UTF-16BE</li>
     *  <li>UTF-16LE</li>
     *  <li>UTF-16</li>
     * </ul>
     * 
     * @param string The Unicode (UTF-8) <code>String</code> to encode.
     * @param charset The <code>Charset</code> in which to encode the input <code>String</code>.
     * @return A representation of the input <code>String</code> in the Charset provided.
     */
    public static String stringEncoder(String string, Charset charset)
    {
        Charset characterSet = charset;
        if(characterSet == null)
        {
            characterSet = Charset.forName(UTF8);
        }
        
        ByteBuffer bb = characterSet.encode (CharBuffer.wrap (string));
        return new String(bb.array()).trim();
    }
    
    /**
     * As <code>stringEncoder(String string, Charset charset)</code> but defaults
     * to UTF-8
     * 
     * @param string
     * @return encoded string
     */
    public static String stringEncoder(String string)
    {
        Charset charset = Charset.forName(UTF8);
        
        ByteBuffer bb = charset.encode (CharBuffer.wrap (string));
        return new String(bb.array()).trim();
    }
    
    /**
     * Decodes the bytes in a String of a given Charset, into Unicode (i.e. UTF-8) 
     * characters. Expected Charset values can be:
     * <ul>
     *  <li>US-ASCII</li>
     *  <li>ISO-8859-1</li>
     *  <li>UTF-8</li>
     *  <li>UTF-16BE</li>
     *  <li>UTF-16LE</li>
     *  <li>UTF-16</li>
     * </ul>
     *  
     * @param string The <code>String</code> to decode to Unicode.
     * @param charset The <code>Charset</code> of the <code>String</code> to decode.
     * @return A Unicode representation of the input <code>String</code>.
     */
    public static String stringDecoder(String string, Charset charset)
    {
        Charset characterSet = charset;
        if(characterSet == null)
        {
            characterSet = Charset.forName(UTF8);
        }
        
        CharBuffer cb = characterSet.decode(ByteBuffer.wrap (string.getBytes()));
        return new String(cb.array());
    }

    /**
     * This method accepts a list of <code>ClientListEntry</code> objects,
     * a list of <code>ClientFilter</code> and applies the filters the
     * entries
     * 
     * @param list The <code>ClientListEntry</code> list to filter
     * @param filters The <code>ClientFilter</code>s to apply on the list
     * @return The filtered list of <code>ClientListEntries</code>
     */
    public static List<ClientListEntry> filterList(List<ClientListEntry> list, List<ClientPolarisedFilter> filters)
    {
        List<ClientListEntry> filteredList = null;
        if (filters != null && filters.size() > 0 && list.size() > 0)
        {
            filteredList = new ArrayList<ClientListEntry>(list);
            for (ClientPolarisedFilter filter : filters)
            {
                filteredList = filter.applyFilter(filteredList);
            }
        }
        return filteredList;
    }
    
    /**
     * Find the parents of this file
     * 
     * @param file
     * @param parents
     */
    public static void findParents(File file, List<File> parents)
    {
        File parentFile = file.getParentFile();
        if (parentFile != null)
        {
            parents.add(parentFile);
            findParents(parentFile, parents);
        }
    }
    
    /**
     * Creates an BaseFileTransferMappedRecord from a ByteArrayOutputStream
     * 
     * Checksum is calculated automatically by the DefaultPayload class
     * 
     * @param uri
     * @param output
     * @return BaseFileTransferMappedRecord
     */
    public static BaseFileTransferMappedRecord createBaseFileTransferMappedRecord(URI uri, ByteArrayOutputStream output)
    {
        File srcFile = new File((uri).getPath());
        String fileName = srcFile.getName();

        BaseFileTransferMappedRecord record = new BaseFileTransferMappedRecord();
        record.setRecordName(uri.toString());
        record.setRecordShortDescription("");
        record.setContent(output.toByteArray());
        record.setName(fileName);
        record.setCreatedDayTime(new Date());
        record.setSize(output.toByteArray().length);
        return record;
    }
    
}
