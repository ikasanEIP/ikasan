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
package org.ikasan.security.service.authentication;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.PolicyLink;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public class IkasanAuthentication implements Authentication
{
    private static Logger logger = LoggerFactory.getLogger(IkasanAuthentication.class);

    public static final String MODULE = "Module";
	public static final String MAPPING_CONFIGURATION = "Mapping Configuration";
	public static final String FLOW = "Flow";
	public static final String BUSINESS_STREAM = "Business Stream";


	private boolean isAuthenticated;
	private List<GrantedAuthority> authorities;
	private Principal principal;
	private String credentials;
	private long previousLoginTimestamp;
	
	/**
	 * @param isAuthenticated
	 * @param authorities
	 * @param principal
	 */
	public IkasanAuthentication(boolean isAuthenticated,
			Principal principal, List<GrantedAuthority> authorities,
			String credentials, long previousLoginTimestamp)
	{
		super();
		this.isAuthenticated = isAuthenticated;
		this.authorities = authorities;
		this.principal = principal;
		this.credentials = credentials;
		this.previousLoginTimestamp = previousLoginTimestamp;
	}
	
    /* (non-Javadoc)
     * @see java.security.Principal#getName()
     */
    @Override
    public String getName()
    {
        return principal.getName();
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return authorities;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials()
    {
        return this.credentials;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getDetails()
     */
    @Override
    public Object getDetails()
    {
    	return null;
        //throw new UnsupportedOperationException();
    }

	/* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal()
    {
        return this.principal;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#isAuthenticated()
     */
    @Override
    public boolean isAuthenticated()
    {
        return isAuthenticated;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#setAuthenticated(boolean)
     */
    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException
    {
    	this.isAuthenticated = isAuthenticated;
    }
	  
    
    /**
	 * @return the previousLoginTimestamp
	 */
	public long getPreviousLoginTimestamp()
	{
		return previousLoginTimestamp;
	}

	/**
	 * @param previousLoginTimestamp the previousLoginTimestamp to set
	 */
	public void setPreviousLoginTimestamp(long previousLoginTimestamp)
	{
		this.previousLoginTimestamp = previousLoginTimestamp;
	}

	/**
     * 
     * @param authorityName
     * @return
     */
    public boolean hasGrantedAuthority(String authorityName)
    {
    	for(GrantedAuthority grantedAuthority: this.getAuthorities())
    	{
    		if(grantedAuthority.getAuthority().equals(authorityName))
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * 
     * @param linkedItemType
     * @param linkedItemId
     * @return
     */
    public boolean canAccessLinkedItem(String linkedItemType, Long linkedItemId)
    {
    	for(GrantedAuthority grantedAuthority: this.getAuthorities())
    	{
    		logger.debug("Policy: " + (Policy)grantedAuthority);
    		
    		PolicyLink policyLink = ((Policy)grantedAuthority).getPolicyLink();
    		
    		logger.debug("PolicyLink: " + policyLink);
    		if(policyLink != null)
    		{
    			if(policyLink.getPolicyLinkType().getName().equals(linkedItemType)
    					&& policyLink.getTargetId().equals(linkedItemId))
    			{
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }

	/**
	 * Get linked module ids.
	 *
	 * @return
     */
    public List<Long> getLinkedModuleIds()
	{
		return this.getLinkedIds(MODULE);
	}

	/**
	 * Get linked flow ids.
	 *
	 * @return
     */
	public List<Long> getLinkedFlowIds()
	{
		return this.getLinkedIds(FLOW);
	}

	/**
	 * Get linked business stream ids.
	 *
	 * @return
     */
	public List<Long> getLinkedBusinessStreamIds()
	{
		return this.getLinkedIds(BUSINESS_STREAM);
	}

	/**
	 * get linked mapping configuration ids.
	 *
	 * @return
     */
	public List<Long> getLinkedMappingConfigurationIds()
	{
		return this.getLinkedIds(MAPPING_CONFIGURATION);
	}

	/**
	 * Helper method to get liked ids.
	 *
	 * @param type
	 * @return
     */
	private List<Long> getLinkedIds(String type)
	{
		ArrayList<Long> id = new ArrayList<>();

		for(GrantedAuthority grantedAuthority: this.getAuthorities())
		{
			PolicyLink policyLink = ((Policy)grantedAuthority).getPolicyLink();

			if(policyLink != null)
			{
				if(policyLink.getPolicyLinkType().getName().equals(type))
				{
					id.add(policyLink.getTargetId());
				}
			}
		}

		return id;
	}
}
