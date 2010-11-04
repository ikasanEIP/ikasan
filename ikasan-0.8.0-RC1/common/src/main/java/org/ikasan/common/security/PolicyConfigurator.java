/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
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
