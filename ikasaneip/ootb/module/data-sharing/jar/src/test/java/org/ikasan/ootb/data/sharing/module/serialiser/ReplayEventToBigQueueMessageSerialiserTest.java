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
package org.ikasan.ootb.data.sharing.module.serialiser;

import org.ikasan.dashboard.dto.replay.ReplayEventImpl;
import org.ikasan.spec.replay.ReplayEvent;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

public class ReplayEventToBigQueueMessageSerialiserTest {
    private final ReplayEventToBigQueueMessageSerialiser serialiser =
        new ReplayEventToBigQueueMessageSerialiser();

    @Test
    public void preservesReplayFieldsAndPayload() {
        ReplayEventImpl event = new ReplayEventImpl();
        event.setId("replay-1");
        event.setModuleName("module-1");
        event.setFlowName("flow-1");
        event.setEventId("event-1");
        event.setTimestamp(123456789L);
        event.setExpiry(987654321L);
        event.setEventAsString("Replay payload: £ €");
        event.setEvent("Replay payload: £ € \n\"quoted\"".getBytes(StandardCharsets.UTF_8));

        List<ReplayEvent> restored = serialiser.deserialise(serialiser.serialise(List.of(event)));

        assertEquals(1, restored.size());
        ReplayEvent actual = restored.get(0);
        assertEquals(event.getId(), actual.getId());
        assertEquals(event.getModuleName(), actual.getModuleName());
        assertEquals(event.getFlowName(), actual.getFlowName());
        assertEquals(event.getEventId(), actual.getEventId());
        assertEquals(event.getTimestamp(), actual.getTimestamp());
        assertEquals(event.getExpiry(), actual.getExpiry());
        assertEquals(event.getEventAsString(), actual.getEventAsString());
        assertArrayEquals(event.getEvent(), actual.getEvent());
    }

    @Test
    public void preservesEmptyCheckpointBatch() {
        assertTrue(serialiser.deserialise(serialiser.serialise(List.of())).isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void rejectsInvalidQueueMessage() {
        serialiser.deserialise("invalid json".getBytes(StandardCharsets.UTF_8));
    }
}
