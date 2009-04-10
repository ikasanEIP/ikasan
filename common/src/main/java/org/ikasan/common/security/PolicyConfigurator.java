/*
 * $Id: PolicyConfigurator.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/security/PolicyConfigurator.java $
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

import org.ikasan.common.ResourceLoader;
import org.ikasan.common.security.policy.EncryptionPolicy;

/**
 * Policy configurator maps the authentication policy to the application policy
 * to obtain the required authentication algorithm.
 * 
 * @author <a href="mailto:info@ikasan.org">Madhu Konda</a>
 *
 */        
public class PolicyConfigurator
{
    /** Serialization */
    private static final long serialVersionUID = 1L;
    /** security authenticationPolicy instance */
    private AuthenticationPolicy authenticationPolicy;
    /** security credential instance */
    private IkasanPasswordCredential ikasanPasswordCredential;
    /** encryption security policy instance */
    private EncryptionPolicy encryptionPolicy;

    /**
     * This method is usually called from the application server as well as from
     * command-line-interface clients.
     * 
     * The CLI clients need to wrap the respective arguments in the Map
     * 
     * @param options
     */
    public void initPolicy(Map<String, String> options)
    {
        this.initPolicy(
                AuthenticationPolicyFactory.getAuthenticationPolicy(options),
                CredentialFactory.getPasswordCredential(options) );
    }

    /**
     * This method is usually called from the application server as well as from
     * command-line-interface clients.
     * 
     * The CLI clients need to wrap the respective arguments in the Map
     * 
     * @param authentPolicy 
     * @param ikasanPwdCredential 
     */
    public void initPolicy(AuthenticationPolicy authentPolicy, IkasanPasswordCredential ikasanPwdCredential)
    {
        try
        {
            this.setAuthenticationPolicy(authentPolicy);
            this.setCredential(ikasanPwdCredential);
            String policyName = this.authenticationPolicy.getPolicyName();
            EncryptionPolicy ep = 
                ResourceLoader.getInstance().getIkasanSecurityService().getEncryptionPolicy(policyName);
            
            this.setEncryptionPolicy(ep);
        }
        catch(EncryptionPolicyNotFoundException e)
        {
            throw new IllegalArgumentException(e);
        }
        catch(SecurityNotConfiguredException e)
        {
            throw new IllegalArgumentException(e);
        }
    }

//    /**
//     * Load the supported encryption policies.
//     * @return Policy
//     */
//    private Policy loadEncryptionPolicies()
//    {
//        boolean screamOnFail = true;
//        
//        try
//        {
//            // load the valid encryption policies
//            URL xmlURL = ResourceLoader.getInstance().getAsUrl(getAuthenticationPolicy().getEncryptionPoliciesURL(), screamOnFail);
//        
//            // set entity resolver to use our default override
//            CommonXMLParser parser = ResourceLoader.getInstance().newXMLParser();
//            parser.setEntityResolver();
//            parser.setValidation(true, XMLConstants.W3C_XML_SCHEMA_NS_URI);
//            parser.setNamespaceAware(Boolean.TRUE);
//        
//            Document doc = parser.parse(xmlURL.toString());
//
//            // Create a Policy object using XStream.
//            // No need to validate as it has been previously validated.
//            
//            Policy encryptionPolicies = new Policy().fromXML(doc, false);
//            logger.debug("Encryption Policy 'xstreamed' from the doc [" + encryptionPolicies.toString() + ']');
//            return encryptionPolicies;
//        }
//        catch(ParserConfigurationException e)
//        {
//            throw new CommonRuntimeException(e);
//        }
//        catch(SAXException e)
//        {
//            throw new CommonRuntimeException(e);
//        }
//        catch(TransformerException e)
//        {
//            throw new CommonRuntimeException(e);
//        }
//        catch(IOException e)
//        {
//            throw new CommonRuntimeException(e);
//        }
//    }
//    
//    /**
//     * Method to match the application policy
//     * 
//     * @param policy 
//     * @return ApplicationPolicy
//     */
//    private ApplicationPolicy matchPolicy(Policy policy)
//    {
//        logger.debug("Searching on authentication policy [" 
//                + this.authenticationPolicy.getPolicyName() + ']');
//        
//        List<ApplicationPolicy> policyList = policy.getApplicationPolicies();
//        for (ApplicationPolicy appPolicy : policyList)
//        {
//            if (this.authenticationPolicy.getPolicyName().equals(appPolicy.getName()))
//            {
//                logger.debug("Matching policy found  for [" 
//                        + this.authenticationPolicy.getPolicyName() + ']');
//                return appPolicy;
//            }
//        }
//
//        logger.warn("Authentication Policy [" 
//                + this.authenticationPolicy.getPolicyName() + "] not found in ["
//                + authenticationPolicy.getEncryptionPoliciesURL() + "]. " +
//                "Supported policies [" + policy.getApplicationPolicies());
//
//        return null;
//    }

    /**
     * Setter for matched encryption policy
     * 
     * @param encryptionPolicy
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
     * @param authenticationPolicy
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
     * @param ikasanPasswordCredential 
     */
    public void setCredential(final IkasanPasswordCredential ikasanPasswordCredential)
    {
        this.ikasanPasswordCredential = ikasanPasswordCredential;
    }

    /**
     * This method encodes the raw password into an encrypted form. This method
     * always uses salt and iteration count for creating the encrypted password
     * 
     * @param rawPassword
     * @return encoded text
     * 
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String encode(final String rawPassword) throws InvalidKeyException,
            NoSuchAlgorithmException, InvalidKeySpecException,
            InvalidAlgorithmParameterException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException
    {
        return (String) getEncryptionPolicy().getAlgorithm().encode(
            rawPassword);
    }

    /**
     * This method decodes the encoded password into a human readable text
     * format. This method always assumes that salt and iteration count are
     * provided
     * 
     * @param encodedPassword
     * @return decoded text
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public String decode(final String encodedPassword) throws InvalidKeyException,
            InvalidKeySpecException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException
    {
        return (String) getEncryptionPolicy().getAlgorithm().decode(
            encodedPassword);
    }
}
