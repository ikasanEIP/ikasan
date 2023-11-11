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
package org.ikasan.cli.shell.operation.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This test class supports the <code>IkasanProcess</code> class.
 * 
 * @author Ikasan Development Team
 */
class IkasanProcessTest
{
    @Test
    void successful_instantiaion()
    {
        IkasanProcess ikasanProcess1 = new IkasanProcess("type", "name", 12345, "user");
        IkasanProcess ikasanProcess2 = new IkasanProcess("type2", "name2", 123456, "user2");
        IkasanProcess ikasanProcess3 = new IkasanProcess("type", "name", 12345, "user");

        assertEquals(ikasanProcess1.getName(), ikasanProcess3.getName());
        assertEquals(ikasanProcess1.getPid(), ikasanProcess3.getPid());
        assertEquals(ikasanProcess1.getType(), ikasanProcess3.getType());
        assertEquals(ikasanProcess1.getUser(), ikasanProcess3.getUser());

        assertEquals(ikasanProcess1, ikasanProcess3);

        assertNotEquals(ikasanProcess2.getName(), ikasanProcess3.getName());
        assertNotNull(ikasanProcess3.getPid());
        assertNotEquals(ikasanProcess2.getType(), ikasanProcess3.getType());
        assertNotEquals(ikasanProcess2.getUser(), ikasanProcess3.getUser());

        assertNotEquals(ikasanProcess2, ikasanProcess3);
    }
}

