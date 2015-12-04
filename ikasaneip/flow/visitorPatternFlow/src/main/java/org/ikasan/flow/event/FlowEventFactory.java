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
package org.ikasan.flow.event;

import org.ikasan.spec.event.EventFactory;
import org.ikasan.spec.flow.FlowEvent;

import java.io.Serializable;

/**
 * Implementation of the EventFactory contract based on the creation 
 * of a FlowEvent.
 * 
 * @author Ikasan Development Team
 *
 */
public class FlowEventFactory implements EventFactory<FlowEvent<?,?>>
{
    /**
     * Factory method to create a new FlowEvent instance.
     * @param identifier
     * @param payload
     */
    public <IDENTIFIER,PAYLOAD> FlowEvent<IDENTIFIER,PAYLOAD> newEvent(IDENTIFIER identifier, PAYLOAD payload)
    {
        return new GenericFlowEvent<>(identifier, payload);
    }

    public <IDENTIFIER,PAYLOAD> FlowEvent<IDENTIFIER, PAYLOAD> newEvent(IDENTIFIER identifier, IDENTIFIER relatedIdentifier, PAYLOAD payload)
    {
        return new GenericFlowEvent<>(identifier, relatedIdentifier, payload);
    }

	/**
	 * Implementation of a flowEvent based on payload being of any generic type.
	 * 
	 * @author Ikasan Development Team
	 *
	 */
	private class GenericFlowEvent<ID,PAYLOAD> implements FlowEvent<ID,PAYLOAD>, Serializable
	{
		/** default serial id */
        private static final long serialVersionUID = 1L;

        /** immutable identifier */
        private ID identifier;

        /** immutable related identifier */
        private ID relatedIdentifier;

		/** immutable event creation timestamp */
	    private long timestamp;

	    /** payload */
	    private PAYLOAD payload;

        /**
         * Constructor
         * @param identifier
         * @param payload
         */
        protected GenericFlowEvent(ID identifier, PAYLOAD payload)
        {
            this.identifier = identifier;
            if(identifier == null)
            {
                throw new IllegalArgumentException("identifier cannot be 'null'. Make sure the FlowEvent has an identifier!");
            }
            this.timestamp = System.currentTimeMillis();
            this.payload = payload;
        }

        /**
         * Constructor
         * @param identifier
         * @param relatedIdentifier
         * @param payload
         */
        protected GenericFlowEvent(ID identifier, ID relatedIdentifier, PAYLOAD payload)
        {
            this.identifier = identifier;
            if(identifier == null)
            {
                throw new IllegalArgumentException("identifier cannot be 'null'. Make sure the FlowEvent has an identifier!");
            }
            this.relatedIdentifier = relatedIdentifier;
            this.timestamp = System.currentTimeMillis();
            this.payload = payload;
        }
        
		/**
		 * Get immutable flow event identifier.
		 * @return String - event identifier
		 */
		public ID getIdentifier()
		{
		    return this.identifier;
		}

        /**
         * Get immutable flow event related identifier.
         * @return String - event related identifier
         */
        public ID getRelatedIdentifier()
        {
            return this.relatedIdentifier;
        }

		/**
		 * Get the immutable created date/time of the flow event.
		 * @return long - create date time
		 */
		public long getTimestamp()
		{
		    return this.timestamp;
		}

		/**
		 * Get the payload of this flow event.
		 * @return PAYLOAD payload
		 */
		public PAYLOAD getPayload()
		{
		    return this.payload;
		}
		
		/**
		 * Set the payload of this flow event.
		 * @param payload - payload
		 */
		public void setPayload(PAYLOAD payload)
		{
		    this.payload = payload;
		}

        @Override
        public void replace(FlowEvent<ID, PAYLOAD> flowEvent)
        {
            if (flowEvent != null)
            {
                this.setPayload(flowEvent.getPayload());

                this.identifier = flowEvent.getIdentifier();
                this.timestamp = flowEvent.getTimestamp();
                this.relatedIdentifier = flowEvent.getRelatedIdentifier();
            }
        }

        @Override
        public java.lang.String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("GenericFlowEvent [identifier=");
            builder.append(identifier);
            builder.append(", relatedIdentifier=");
            builder.append(relatedIdentifier);
            builder.append(", timestamp=");
            builder.append(timestamp);
            builder.append(", payload=");
            builder.append(payload);
            builder.append("]");
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GenericFlowEvent that = (GenericFlowEvent) o;

            if (!identifier.equals(that.identifier)) return false;
            if (relatedIdentifier != null ? !relatedIdentifier.equals(that.relatedIdentifier) : that.relatedIdentifier != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = identifier.hashCode();
            result = 31 * result + (relatedIdentifier != null ? relatedIdentifier.hashCode() : 0);
            return result;
        }
    }

}
