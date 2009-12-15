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
package org.ikasan.tools.messaging.repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


import org.ikasan.tools.messaging.model.MessageWrapper;
import org.ikasan.tools.messaging.serialisation.MessageXmlSerialiser;

public class FileSystemMessageRepository extends BaseRepository implements MessageRepository{
		
	private static final String FILE_EXTENSION = ".xml";

	private File directory;
	
	private String directoryPath;
	
	private MessageXmlSerialiser messageXmlSerialiser;

	public FileSystemMessageRepository(String name, String directoryPath, MessageXmlSerialiser messageXmlSerialiser) {
		super(name);
		setDirectoryPath(directoryPath);
		this.messageXmlSerialiser = messageXmlSerialiser;
		if (!directory.isDirectory()){
			throw new IllegalArgumentException(directory+" is not a directory");
		}
	}

	public void setDirectoryPath(String directoryPath) {
		this.directoryPath=directoryPath;
		this.directory = new File(directoryPath);
	}
	
	public String getDirectoryPath(){
		return directoryPath;
	}

	public void save(MessageWrapper message) {
		String xml = messageXmlSerialiser.toXml(message);
		
		File outputFile = new File(directory, message.getMessageId()+FILE_EXTENSION);
		
		try {
			Writer output = new BufferedWriter(new FileWriter(outputFile));
			try {
			  output.write( xml );
			}
			finally {
			  output.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	public List<String> getMessages(){
		List<String> result = new ArrayList<String>();
		
		String[] listing = directory.list(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.endsWith(FILE_EXTENSION);
			}
		});
		
		for (String filename : listing){
			String messageId = filename.substring(0, filename.length()-FILE_EXTENSION.length());
			result.add(messageId);
		}

		return result;
	}
	
	public MessageWrapper getMessage(String messageId){
		
		File messageFile = new File(directory, messageId+FILE_EXTENSION);
		
		StringBuilder contents = new StringBuilder();
	    
	    try {
	     
	      BufferedReader input =  new BufferedReader(new FileReader(messageFile));
	      try {
	        String line = null; 
	        while (( line = input.readLine()) != null){
	          contents.append(line);
	          contents.append(System.getProperty("line.separator"));
	        }
	      }
	      finally {
	        input.close();
	      }
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }

		MessageWrapper messageObject = messageXmlSerialiser.getMessageObject(contents.toString());
		
		
		return messageObject;
	}

}
