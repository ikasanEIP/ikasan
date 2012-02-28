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
package org.ikasan.connector.base.outbound;

import javax.resource.cci.ResourceAdapterMetaData;

/**
 * Implementation class provides info about the capabilities of a resource
 * adapter.
 * 
 * Sub-class must provide values for abstract methods for a particular adapter.
 * Also optionally override supportXXX() methods when necessary.
 * @author Ikasan Development Team
 */
public abstract class EISResourceAdapterMetaData
    implements ResourceAdapterMetaData 
{
    /** Vendor Name */
    private static final String	VENDOR_NAME = "Ikasan";
    /** JCA Specification Version */
    private static final String	JCA_SPEC_VERSION = "1.5";

    /**
     * Returns the version of this adapter.
     *
     * @return	the version of this adapter, in String
     */
    public abstract String getAdapterVersion();

    /**
     * Returns the vendor name of this adapter.
     *
     * @return	the vendor name of this adapter, in String
     */
    public String getAdapterVendorName() {
        return VENDOR_NAME;
    }

    /**
     * Returns the name of this adapter.
     *
     * @return	the name of this adapter, in String
     */
    public abstract String getAdapterName();

    /**
     * Returns the short description of this adapter.
     *
     * @return	the short description of this adapter, in String
     */
    public abstract String getAdapterShortDescription();

    /**
     * Returns the version of the Connector Architecture Specification
     * that is spported by the adapter.
     *
     * @return	the version of the JCA spec supported by this adapter, in String
     */
    public String getSpecVersion() 
    {
        return JCA_SPEC_VERSION;
    }

    /**
     * Returns the fully-qualified names of InteractionSpec types supported
     * by the CCI implementation for this adapter.
     *
     * @return	the fully-qualified names of supported InteractionSpec types,
     *		in String[]
     */
    public abstract String[] getInteractionSpecsSupported();

    /**
     * Returns if the Interaction implementation of this adapter supports the
     *<p>
     *	public boolean execute(InteractionSpec is, Record input, Record output)
     *<p>
     * method.
     *
     * @return	true => support the 3-argument execute() method; false otherwise
     */
    public boolean supportsExecuteWithInputAndOutputRecord() {
        return true;
    }

    /**
     * Returns if the Interaction implementation of this adapter supports the
     *<p>
     *	public Record execute(InteractionSpec iSpec, Record input)
     *<p>
     * method.
     *
     * @return	true => support the 2-argument execute() method; false otherwise
     */
    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }

    /**
     * Returns if this adapter implements the LocalTransaction interface and
     * supports local transaction demarcation on the underlying EIS instance
     * through the LocalTransaction interface.
     *
     * @return	true => support local transaction; false => otherwise
     */
    public boolean supportsLocalTransactionDemarcation() {
        return true;
    }
}
