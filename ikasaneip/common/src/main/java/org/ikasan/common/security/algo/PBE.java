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
package org.ikasan.common.security.algo;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

/**
 * This class represents the (<code>PBE</code>) encryption mechanism.
 * 
 * PBE encryption is essentially two-way encrytpion - that is, you need a
 * passphrase (password/passcode) to do the encryption.
 * 
 * Similarly, in order to perform decryption, you need the same passphrase.
 * 
 * @author Ikasan Development Team
 */
public class PBE extends Algorithm
{
    /** Logger */
    private static Logger logger = Logger.getLogger(PBE.class);
    /**
     * The PBE specific iteration count
     */
    private Integer iterationCount;
    /**
     * The Salt
     */
    private String salt;

    /**
     * Creates a new <code>PBE</code> algorithm instance.
     */
    public PBE()
    {
        // Do nothing
    }

    /**
     * Sets the iteration count.
     * 
     * @param iterationCount
     */
    public void setIterationCount(final Integer iterationCount)
    {
        this.iterationCount = iterationCount;
        logger.debug("Setting iterationCount [" + this.iterationCount + "].");
    }

    /**
     * Returns the iteration count.
     * 
     * @return iterationCount
     */
    public Integer getIterationCount()
    {
        logger.debug("Getting iterationCount [" + this.iterationCount + "].");
        return this.iterationCount;
    }

    /**
     * Sets the salt.
     * 
     * @param salt
     */
    public void setSalt(final String salt)
    {
        this.salt = salt;
        logger.debug("Setting salt [" + this.salt + "].");
    }

    /**
     * Returns the salt.
     * 
     * @return salt
     */
    public String getSalt()
    {
        logger.debug("Getting salt [" + this.salt + "].");
        return this.salt;
    }

    /**
     * Returns a string representation of this object.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append("iterationCount [");
        sb.append(this.getIterationCount());
        sb.append("]\n");
        sb.append("salt [");
        sb.append(this.getSalt());
        sb.append("]\n");
        return sb.toString();
    }

    /**
     * PBE equality test
     * 
     * @param pbe
     * @return true if the PBE is equal ot the one passed in
     */
    public boolean equals(final PBE pbe)
    {
        if (super.equals(pbe) && (this.getIterationCount() == null && pbe.getIterationCount() == null) || this.getIterationCount() != null
                && this.getIterationCount().equals(pbe.getIterationCount()) && (this.getSalt() == null && pbe.getSalt() == null) || this.getSalt() != null
                && this.getSalt().equals(pbe.getSalt())) return true;
        return false;
    }

    /**
     * Sets the common properties for the toXML/fromXML XStream object
     * 
     * @param xstream
     */
    @Override
    protected void setXstreamProps(XStream xstream)
    {
        xstream.registerConverter(new PBEConverter());
        logger.debug("Registering the PBEConverter");
        xstream.alias(this.getClass().getSimpleName(), this.getClass());
    }

    /**
     * This method encrypts and encodes the raw text passed.
     * 
     * @param rawText
     * @return encoded string
     * 
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Override
    public String encode(String rawText) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        /*
         * The PBE algorithm only takes lower 8 bytes.
         * 
         * The PBE mechanism defined in PKCS #5 looks at only the low order 8
         * bits of each character, whereas PKCS #12 looks at all 16 bits of each
         * character.
         * 
         * We use the former and hence we generate the salt with first 8 bytes.
         */
        byte[] pbeSalt = this.getSalt().substring(0, 8).getBytes();
        PBEParameterSpec spec = new PBEParameterSpec(pbeSalt, getIterationCount().intValue());
        PBEKeySpec keySpec = new PBEKeySpec(getPass().toCharArray());
        
        /*
         * Create the PBE secret key
         */
        SecretKeyFactory factory;
        try
        {
            factory = SecretKeyFactory.getInstance(this.getCipher());
        }
        catch (NoSuchAlgorithmException e)
        {
            // no point in continuing..throw the exception
            throw e;
        }
        SecretKey cipherKey;
        try
        {
            cipherKey = factory.generateSecret(keySpec);
        }
        catch (InvalidKeySpecException e)
        {
            throw e;
        }
        logger.debug("SecretKey generated");
        /*
         * Create the instance of the Cipher - the hash function implementation
         * class.
         * 
         * Initialise this class with the SecretKey key and PBEKeySpec
         */
        Cipher cipher;
        byte[] encoding;
        try
        {
            cipher = Cipher.getInstance(this.getCipher());
            cipher.init(Cipher.ENCRYPT_MODE, cipherKey, spec);
            /*
             * Here's where you do the encoding.
             * 
             * The encoding is produced in byte sequence and hence use the
             * Utility class's conversion method to convert this byte sequence
             * to String equivalent.
             */
            encoding = cipher.doFinal(rawText.getBytes());
        }
        catch (NoSuchPaddingException e)
        {
            throw e;
        }
        catch (InvalidKeyException e)
        {
            throw e;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            throw e;
        }
        catch (IllegalBlockSizeException e)
        {
            throw e;
        }
        catch (BadPaddingException e)
        {
            throw e;
        }
        logger.debug("Encoding successful ");
        return new String(Base64.encodeBase64(encoding));
    }

    /**
     * This method decrypts and decodes the encoded text.
     * 
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Override
    public Object decode(String encodedText) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
        /*
         * Create the PBE secret key using the salt and iteration count
         */
        byte[] pbeSalt = this.getSalt().substring(0, 8).getBytes();
        PBEParameterSpec cipherSpec = new PBEParameterSpec(pbeSalt, getIterationCount());
        PBEKeySpec keySpec = new PBEKeySpec(this.getPass().toCharArray());
        SecretKeyFactory factory;
        SecretKey cipherKey;
        try
        {
            factory = SecretKeyFactory.getInstance(getCipher());
            cipherKey = factory.generateSecret(keySpec);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw e;
        }
        catch (InvalidKeySpecException e)
        {
            throw e;
        }
        logger.debug("SecretKey generated");
        /*
         * Decode the text from Base64
         */
        byte[] decodedText = Base64.decodeBase64(encodedText.getBytes());
        /*
         * Now, decrypt the decoded text
         */
        Cipher cipher = null;
        byte[] decode;
        try
        {
            cipher = Cipher.getInstance(getCipher());
            cipher.init(Cipher.DECRYPT_MODE, cipherKey, cipherSpec);
            decode = cipher.doFinal(decodedText);
        }
        catch (NoSuchPaddingException e)
        {
            throw e;
        }
        catch (InvalidKeyException e)
        {
            throw e;
        }
        catch (InvalidAlgorithmParameterException e)
        {
            throw e;
        }
        catch (IllegalBlockSizeException e)
        {
            throw e;
        }
        catch (BadPaddingException e)
        {
            throw e;
        }
        logger.debug("Decoding successful ");
        return new String(decode);
    }
}
