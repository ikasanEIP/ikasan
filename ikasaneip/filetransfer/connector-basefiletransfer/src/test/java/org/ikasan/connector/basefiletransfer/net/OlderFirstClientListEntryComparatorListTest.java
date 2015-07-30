/*
 * $Id:$
 * $URL:$
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
package org.ikasan.connector.basefiletransfer.net;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Tests OlderFirstClientListEntryComparator
 */
public class OlderFirstClientListEntryComparatorListTest
{
    private OlderFirstClientListEntryComparator uut = new OlderFirstClientListEntryComparator();

    @Test
    public void compare_when_firstfile_is_older(){
        ClientListEntry firstClientListEntry =new ClientListEntry();
        firstClientListEntry.setMtime(1438155532);
        firstClientListEntry.setName("A");
        ClientListEntry secondClientListEntry =new ClientListEntry();
        secondClientListEntry.setMtime(1439155532);
        secondClientListEntry.setName("B");

        List<ClientListEntry> list = new ArrayList<ClientListEntry>();
        list.add(firstClientListEntry);
        list.add(secondClientListEntry);

        //do test
        Collections.sort(list, uut);

        assertEquals("A", list.get(0).getName());
        assertEquals("B", list.get(1).getName());

    }

    @Test
    public void compare_when_secondfile_is_older(){
        ClientListEntry firstClientListEntry =new ClientListEntry();
        firstClientListEntry.setMtime(1439155532);
        firstClientListEntry.setName("A");
        ClientListEntry secondClientListEntry =new ClientListEntry();
        secondClientListEntry.setMtime(1438155532);
        secondClientListEntry.setName("B");

        List<ClientListEntry> list = new ArrayList<ClientListEntry>();
        list.add(firstClientListEntry);
        list.add(secondClientListEntry);

        //do test
        Collections.sort(list, uut);

        assertEquals("B", list.get(0).getName());
        assertEquals("A", list.get(1).getName());

    }

    @Test
    public void compare_when_files_have_same_age(){
        ClientListEntry firstClientListEntry =new ClientListEntry();
        firstClientListEntry.setMtime(1439155532);
        firstClientListEntry.setName("A");
        ClientListEntry secondClientListEntry =new ClientListEntry();
        secondClientListEntry.setMtime(1439155532);
        secondClientListEntry.setName("B");

        List<ClientListEntry> list = new ArrayList<ClientListEntry>();
        list.add(firstClientListEntry);
        list.add(secondClientListEntry);

        //do test
        Collections.sort(list, uut);

        assertEquals("A", list.get(0).getName());
        assertEquals("B", list.get(1).getName());

    }

    @Test
    public void compare_when_files_have_same_age_and_same_name(){
        ClientListEntry firstClientListEntry =new ClientListEntry();
        firstClientListEntry.setMtime(1439155532);
        firstClientListEntry.setName("A");
        ClientListEntry secondClientListEntry =new ClientListEntry();
        secondClientListEntry.setMtime(1439155532);
        secondClientListEntry.setName("A");

        List<ClientListEntry> list = new ArrayList<ClientListEntry>();
        list.add(firstClientListEntry);
        list.add(secondClientListEntry);

        //do test
        Collections.sort(list, uut);

        assertEquals("A", list.get(0).getName());
        assertEquals("A", list.get(1).getName());

    }
}
