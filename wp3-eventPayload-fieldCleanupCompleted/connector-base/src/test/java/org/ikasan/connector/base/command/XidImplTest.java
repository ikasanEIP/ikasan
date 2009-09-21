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
package org.ikasan.connector.base.command;

import javax.transaction.xa.Xid;

import junit.framework.TestCase;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

/**
 * Test class for XidImpl 
 * @author Ikasan Development Team
 *
 */
public class XidImplTest extends TestCase
{
    /**
     * arbitrary global transaction id
     */
    byte [] globalTransactionId = "globalTrnsactionId".getBytes();
    
    /**
     * arbitrary branch qualifier
     */
    byte [] branchQualifier = "branchQualifier".getBytes();
    
    /**
     * arbitrary format id
     */
    int formatId = 99;
    
    
    /**
     * tests the constructor
     */
    @Test
    public void testConstructor_ByteArrayByteArrayInt()
    {

        XidImpl xidImpl = new XidImpl(globalTransactionId, branchQualifier, formatId);
        
        assertEquals("first argument of constructor should set the global transaction id", globalTransactionId, xidImpl.getGlobalTransactionId());
        assertEquals("second argument of constructor should set the branch qualifier ", branchQualifier, xidImpl.getBranchQualifier());
        assertEquals("third argument of constructor should set the formatId ", formatId, xidImpl.getFormatId());

    }

    /**
     * tests the constructor
     */
    @Test
    public void testConstructor_Xid()
    {
       Mockery interfaceMockery = new Mockery();
       final Xid otherXid = interfaceMockery.mock(Xid.class);
       
       interfaceMockery.checking(new Expectations()
       { 
           {
               allowing(otherXid).getGlobalTransactionId();
               will(returnValue(globalTransactionId));

               allowing(otherXid).getBranchQualifier();
               will(returnValue(branchQualifier));
               
               allowing(otherXid).getFormatId();
               will(returnValue(formatId));
           }
       });
       
       XidImpl xidImpl = new XidImpl(otherXid);
       
       assertEquals("new XidImp global transaction id should be that of constructor argument", otherXid.getGlobalTransactionId(), xidImpl.getGlobalTransactionId());
       assertEquals("new XidImp branch qualifier should be that of constructor argument", otherXid.getBranchQualifier(), xidImpl.getBranchQualifier());
       assertEquals("new XidImp format id should be that of constructor argument", otherXid.getFormatId(), xidImpl.getFormatId());

       
    }
}
