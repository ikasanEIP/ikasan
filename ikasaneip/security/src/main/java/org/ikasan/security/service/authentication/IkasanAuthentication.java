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
import java.util.Collection;
import java.util.List;

import org.apache.directory.api.ldap.aci.UserClass.ThisEntry;
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
	private boolean isAuthenticated;
	private List<GrantedAuthority> authorities;
	private Principal principal;
	private String credentials;
	
	/**
	 * @param isAuthenticated
	 * @param authorities
	 * @param principal
	 */
	public IkasanAuthentication(boolean isAuthenticated,
			Principal principal, List<GrantedAuthority> authorities,
			String credentials)
	{
		super();
		this.isAuthenticated = isAuthenticated;
		this.authorities = authorities;
		this.principal = principal;
		this.credentials = credentials;
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
    	throw new UnsupportedOperationException();
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
    		System.out.println("Policy: " + (Policy)grantedAuthority);
    		
    		PolicyLink policyLink = ((Policy)grantedAuthority).getPolicyLink();
    		
    		System.out.println("PolicyLink: " + policyLink);
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
}
