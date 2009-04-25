/*
 * $Id: EISResourceAdapterMetaData.java 16756 2009-04-22 12:35:57Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/connector-base/src/main/java/org/ikasan/connector/base/outbound/EISResourceAdapterMetaData.java $
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
