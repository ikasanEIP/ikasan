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
package org.ikasan.configurationService.util;

import org.ikasan.spec.configuration.Masked;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sample configuration class for testing
 * Ikasan Development Team.
 */
public class ImportExportConfiguration
{
	private String string;
    
    @Masked
    private String maskedString;
    
    private Map<String,String>map;

	private Long longParam;

	private Integer intParam;

	private List<String> list = new ArrayList<String>();

	private Boolean booleanParam;

    public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	public String getString()
    {
        return string;
    }

    public void setString(String string)
    {
        this.string = string;
    }

	/**
	 * @return the maskedString
	 */
	public String getMaskedString()
	{
		return maskedString;
	}

	/**
	 * @param maskedString the maskedString to set
	 */
	public void setMaskedString(String maskedString)
	{
		this.maskedString = maskedString;
	}

	public Long getLongParam() {
		return longParam;
	}

	public void setLongParam(Long longParam) {
		this.longParam = longParam;
	}

	public Integer getIntParam() {
		return intParam;
	}

	public void setIntParam(Integer intParam) {
		this.intParam = intParam;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public Boolean getBooleanParam() {
		return booleanParam;
	}

	public void setBooleanParam(Boolean booleanParam) {
		this.booleanParam = booleanParam;
	}
}
