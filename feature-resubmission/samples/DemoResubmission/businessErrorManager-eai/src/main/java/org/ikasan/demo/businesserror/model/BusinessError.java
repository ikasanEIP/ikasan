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
package org.ikasan.demo.businesserror.model;

import org.apache.log4j.Logger;

public class BusinessError {
	
	private Long id;
	
	private String errorMessage;
	
	private String errorSummary;

	private String originatingSystem;
	
	private boolean resubmittable = true;
	
	private static Logger logger = Logger.getLogger(BusinessError.class);
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	private String externalReference;

	public BusinessError(String originatingSystem, String externalReference, String errorMessage) {
		this.originatingSystem = originatingSystem;
		this.externalReference = externalReference;
		this.errorMessage = errorMessage;
		
		String firstLine = errorMessage;
		int endOfFirstLine = errorMessage.indexOf("\n");
		if (endOfFirstLine>-1){
			firstLine = errorMessage.substring(0, endOfFirstLine);
		}
		this.errorSummary = firstLine;
		if (errorSummary.length()>80){
			errorSummary = errorSummary.substring(0, 77)+"...";
		}
		
	}

	public String getExternalReference() {
		return externalReference;
	}
	
	public String getErrorSummary(){
		return errorSummary;
	}
	

	
	public void setOriginatingSystem(String originatingSystem){
		this.originatingSystem = originatingSystem;
	}
	
	public String getOriginatingSystem(){
		return originatingSystem;
	}

	@Override
	public String toString() {
		return "BusinessError ["
				+ ", errorSummary=" + errorSummary + ", externalReference="
				+ externalReference + ", id=" + id + "]";
	}
	
	public boolean isResubmittable(){
		return resubmittable;
	}




}
