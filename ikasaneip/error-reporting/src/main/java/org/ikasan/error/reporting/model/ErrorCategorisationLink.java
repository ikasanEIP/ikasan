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
package org.ikasan.error.reporting.model;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ErrorCategorisationLink
{
	public static final String RETRY_ACTION = "Retry";
	public static final String STOP_ACTION = "Stop";
	public static final String EXCLUDE_EVENT_ACTION = "ExcludeEvent";
	
	private Long id;
	private String moduleName;
	private String flowName;
	private String flowElementName;
	private String action;
	private String exceptionClass;
	private ErrorCategorisation errorCategorisation;
	
	/**
	 * @param moduleName
	 * @param flowName
	 * @param flowElementName
	 * @param action
	 * @param exceptionClass
	 */
	public ErrorCategorisationLink(String moduleName, String flowName,
			String flowElementName, String action, String exceptionClass)
	{
		super();
		this.moduleName = moduleName;
		this.flowName = flowName;
		this.flowElementName = flowElementName;
		this.action = action;
		this.exceptionClass = exceptionClass;
	}
	
	private ErrorCategorisationLink()
	{
		
	}
	
	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}
	
	/**
	 * @return the moduleName
	 */
	public String getModuleName()
	{
		return moduleName;
	}
	
	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}
	
	/**
	 * @return the flowName
	 */
	public String getFlowName()
	{
		return flowName;
	}
	
	/**
	 * @param flowName the flowName to set
	 */
	public void setFlowName(String flowName)
	{
		this.flowName = flowName;
	}
	
	/**
	 * @return the flowElementName
	 */
	public String getFlowElementName()
	{
		return flowElementName;
	}
	
	/**
	 * @param flowElementName the flowElementName to set
	 */
	public void setFlowElementName(String flowElementName)
	{
		this.flowElementName = flowElementName;
	}
	
	/**
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}
	
	/**
	 * @param action the action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	/**
	 * @return the errorCategorisation
	 */
	public ErrorCategorisation getErrorCategorisation()
	{
		return errorCategorisation;
	}

	/**
	 * @param errorCategorisation the errorCategorisation to set
	 */
	public void setErrorCategorisation(ErrorCategorisation errorCategorisation)
	{
		this.errorCategorisation = errorCategorisation;
	}

	/**
	 * @return the exceptionClass
	 */
	public String getExceptionClass()
	{
		return exceptionClass;
	}

	/**
	 * @param exceptionClass the exceptionClass to set
	 */
	public void setExceptionClass(String exceptionClass)
	{
		this.exceptionClass = exceptionClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime
				* result
				+ ((errorCategorisation == null) ? 0 : errorCategorisation
						.hashCode());
		result = prime * result
				+ ((exceptionClass == null) ? 0 : exceptionClass.hashCode());
		result = prime * result
				+ ((flowElementName == null) ? 0 : flowElementName.hashCode());
		result = prime * result
				+ ((flowName == null) ? 0 : flowName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((moduleName == null) ? 0 : moduleName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorCategorisationLink other = (ErrorCategorisationLink) obj;
		if (action == null)
		{
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (errorCategorisation == null)
		{
			if (other.errorCategorisation != null)
				return false;
		} else if (!errorCategorisation.equals(other.errorCategorisation))
			return false;
		if (exceptionClass == null)
		{
			if (other.exceptionClass != null)
				return false;
		} else if (!exceptionClass.equals(other.exceptionClass))
			return false;
		if (flowElementName == null)
		{
			if (other.flowElementName != null)
				return false;
		} else if (!flowElementName.equals(other.flowElementName))
			return false;
		if (flowName == null)
		{
			if (other.flowName != null)
				return false;
		} else if (!flowName.equals(other.flowName))
			return false;
		if (id == null)
		{
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (moduleName == null)
		{
			if (other.moduleName != null)
				return false;
		} else if (!moduleName.equals(other.moduleName))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ErrorCategorisationLink [id=" + id + ", moduleName="
				+ moduleName + ", flowName=" + flowName + ", flowElementName="
				+ flowElementName + ", action=" + action + ", exceptionClass="
				+ exceptionClass + ", errorCategorisation="
				+ errorCategorisation + "]";
	}

}
