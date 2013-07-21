/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.log4j.Logger;
import org.ikasan.common.ResourceLoader;
import org.ikasan.common.security.policy.EncryptionPolicy;

/**
 * Policy configurator maps the authentication policy to the application policy to obtain the required authentication
 * algorithm.
 * 
 * @author Ikasan Development Team
 */
public class PolicyConfigurator
{
    /** Serialization */
    private static final long serialVersionUID = 1L;

    /** Security authenticationPolicy instance */
    private AuthenticationPolicy authenticationPolicy;

    /** Security credential instance */
    private IkasanPasswordCredential ikasanPasswordCredential;

    /** Encryption security policy instance */
    private EncryptionPolicy encryptionPolicy;

    /** Logger */
    private static Logger logger = Logger.getLogger(PolicyConfigurator.class);
    
    /**
     * This method is usually called from the application server as well as from command-line-interface clients.
     * 
     * The CLI clients need to wrap the respective arguments in the Map
     * 
     * @param options - Map of options to initialise the policy 
     */
    public void initPolicy(Map<String, String> options)
    {
        this.initPolicy(AuthenticationPolicyFactory.getAuthenticationPolicy(options), CredentialFactory
            .getPasswordCredential(options));
    }

    /**
     * This method is usually called from the application server as well as from command-line-interface clients.
     * 
     * The CLI clients need to wrap the respective arguments in the Map
     * 
     * @param authentPolicy - The authentication policy provided 
     * @param ikasanPwdCredential - The password credential
     */
    public void initPolicy(AuthenticationPolicy authentPolicy, IkasanPasswordCredential ikasanPwdCredential)
    {
        try
        {
            this.setAuthenticationPolicy(authentPolicy);
            this.setCredential(ikasanPwdCredential);
            String policyName = this.authenticationPolicy.getPolicyName();
            EncryptionPolicy ep = ResourceLoader.getInstance().getIkasanSecurityService().getEncryptionPolicy(
                policyName);
            this.setEncryptionPolicy(ep);
        }
        catch (EncryptionPolicyNotFoundException e)
        {
            logger.error("EncryptionPolicyNotFoundException " + e.getMessage());
            throw new IllegalArgumentException(e);
        }
        catch (SecurityNotConfiguredException e)
        {
            logger.error("SecurityNotConfiguredException " + e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Setter for matched encryption policy
     * 
     * @param encryptionPolicy - The encryption policy to set
     */
    public void setEncryptionPolicy(final EncryptionPolicy encryptionPolicy)
    {
        this.encryptionPolicy = encryptionPolicy;
    }

    /**
     * Getter for matched application policy
     * 
     * @return policy
     */
    public EncryptionPolicy getEncryptionPolicy()
    {
        return this.encryptionPolicy;
    }

    /**
     * Getter for authenticationPolicy
     * 
     * @return AuthenticationPolicy
     */
    public AuthenticationPolicy getAuthenticationPolicy()
    {
        return this.authenticationPolicy;
    }

    /**
     * Set the authenticationPolicy
     * 
     * @param authenticationPolicy - The authentication policy to set
     */
    public void setAuthenticationPolicy(final AuthenticationPolicy authenticationPolicy)
    {
        this.authenticationPolicy = authenticationPolicy;
    }

    /**
     * Getter for credential
     * 
     * @return IkasanCredential
     */
    public IkasanPasswordCredential getCredential()
    {
        return this.ikasanPasswordCredential;
    }

    /**
     * Set the credential
     * 
     * @param ikasanPasswordCredential - The Ikasan password credential to set
     */
    public void setCredential(final IkasanPasswordCredential ikasanPasswordCredential)
    {
        this.ikasanPasswordCredential = ikasanPasswordCredential;
    }

    /**
     * This method encodes the raw password into an encrypted form. This method always uses salt and iteration count for
     * creating the encrypted password
     * 
     * @param rawPassword - The raw password to encode
     * @return encoded text - The encoded password
     * 
     * @throws BadPaddingException - If bad padding occurs
     * @throws IllegalBlockSizeException - If there's an illegal block size
     * @throws NoSuchPaddingException - If there is no such padding
     * @throws InvalidAlgorithmParameterException - If there's an invalid algorithm parameter
     * @throws InvalidKeySpecException - If there's an invalid key specification
     * @throws NoSuchAlgorithmException - If there's no such algorithm
     * @throws InvalidKeyException - If the key is invalid
     */
    public String encode(final String rawPassword) throws InvalidKeyException, NoSuchAlgorithmException,
            InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException
    {
        return (String) getEncryptionPolicy().getAlgorithm().encode(rawPassword);
    }

    /**
     * This method decodes the encoded password into a human readable text format. This method always assumes that salt
     * and iteration count are provided
     * 
     * @param encodedPassword - The encoded password to decode
     * @return decoded text
     * 
     * @throws BadPaddingException - If bad padding occurs
     * @throws IllegalBlockSizeException - If there's an illegal block size
     * @throws NoSuchPaddingException - If there is no such padding
     * @throws InvalidAlgorithmParameterException - If there's an invalid algorithm parameter
     * @throws InvalidKeySpecException - If there's an invalid key specification
     * @throws NoSuchAlgorithmException - If there's no such algorithm
     * @throws InvalidKeyException - If the key is invalid
     */
    public String decode(final String encodedPassword) throws InvalidKeyException, InvalidKeySpecException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException
    {
        return (String) getEncryptionPolicy().getAlgorithm().decode(encodedPassword);
    }
}
