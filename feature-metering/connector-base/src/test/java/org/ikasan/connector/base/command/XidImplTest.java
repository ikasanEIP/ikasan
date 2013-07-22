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
