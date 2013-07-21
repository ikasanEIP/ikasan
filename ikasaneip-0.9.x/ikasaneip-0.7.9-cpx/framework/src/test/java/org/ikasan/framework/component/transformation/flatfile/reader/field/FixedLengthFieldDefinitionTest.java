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

import static org.junit.Assert.assertEquals;

import org.ikasan.framework.component.transformation.flatfile.reader.field.FixedLengthFieldDefinition;
import org.junit.Test;

/**
 * Test class for FixedLengthFieldDefinition
 * 
 * @author Ikasan Development Team
 *
 */
public class FixedLengthFieldDefinitionTest {

	/**
	 * Simply tests that the constructor, and the getters are doing their usual work
	 */
	@Test
	public void testAccessors() {
		String fieldName="fieldName";
		int fieldLength=99;
		FixedLengthFieldDefinition fieldDefinition = new FixedLengthFieldDefinition(fieldName, fieldLength);
		assertEquals("getFieldName should return fieldName set on constructor", fieldName, fieldDefinition.getFieldName());
		assertEquals("getFieldLength should return fieldLength set on constructor", fieldLength, fieldDefinition.getFieldLength());
	}


}
