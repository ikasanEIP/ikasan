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

package org.ikasan.framework.component.transformation.flatfile.reader.field;

/**
 * Simple definition of a fixed length field in a flat file
 * 
 * @author Ikasan Development Team
 *
 */
public class FixedLengthFieldDefinition extends BaseFieldDefinition{

	/**
	 * length of field
	 */
	private int fieldLength;
	
	/**
	 * Constructor 
	 * 
	 * @param fieldName
	 * @param fieldLength
	 */
	public FixedLengthFieldDefinition(String fieldName, int fieldLength) {
		super(fieldName);
		this.fieldLength = fieldLength;
	}


	
	/**
	 * Accessor for fieldLength
	 * 
	 * @return fieldLength
	 */
	public int getFieldLength() {
		return fieldLength;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
    public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getClass().getSimpleName());
		sb.append("[");
		sb.append("fieldPath=");
		sb.append(fieldName);
		sb.append(",");
		sb.append("fieldLength=");
		sb.append(fieldLength);
		sb.append(",");
		sb.append("optional=");
		sb.append(optionalField);
		sb.append(",");
		sb.append("skipIfEmpty=");
		sb.append(skipIfEmpty);
		sb.append("]");
		return sb.toString();
	}
	
}
