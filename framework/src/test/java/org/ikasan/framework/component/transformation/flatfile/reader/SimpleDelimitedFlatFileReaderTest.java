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
package org.ikasan.framework.component.transformation.flatfile.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.ikasan.framework.component.transformation.flatfile.reader.field.BaseFieldDefinition;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Test class for SimpleDelimitedFlatFileReader
 * 
 * @author Ikasan Development Team
 *
 */
public class SimpleDelimitedFlatFileReaderTest {

	/** Mockery for testing */
    Mockery mockery = new Mockery();
	
    /** ContentHandler (mocked) */
	ContentHandler contentHandler = mockery.mock(ContentHandler.class);
	
	/**
	 * Tests the ParseInputSource method by parsing known content with fixed length fields
	 * 
	 * ie our our content is "Rosco P. Coltrane$Hazzard County Sheriff$35"
	 * 
	 * our field definitions are:
	 * 		name 		
	 * 		occupation 	
	 * 		age 		
	 * 
	 * delimiter is '$'
	 * 
	 * we want to produce:
	 * 
	 * <output>
	 *     <name>Rosco P. Coltrane</name>
	 *     <occupation>Hazzard County Sheriff</occupation>
	 *     <age>35</age>
	 * </output>
	 * 
	 * @throws SAXException 
	 * @throws IOException 
	 */
	@Test
	public void testParseInputSource() throws IOException, SAXException {
		
		final String rootElementName = "output";
		final String nameFieldName = "name";
		final String ageFieldName = "age";
		final String occupationFieldName = "occupation";
		
		final String delimiter = "$";
		
		
		
		
		final String nameFieldValue="Rosco P. Coltrane";
		final char[] nameCharacters = nameFieldValue.toCharArray();
		
		final String occupationFieldValue="Hazzard County Sheriff";
		final char[] occupationCharacters = occupationFieldValue.toCharArray();
		
		final String ageFieldValue="35";
		final char[] ageCharacters = ageFieldValue.toCharArray();
		
		
		byte [] flatFileContent = (nameFieldValue+delimiter+occupationFieldValue+delimiter+ageFieldValue).getBytes();
		
		BaseFieldDefinition nameFieldDefinition = new BaseFieldDefinition(nameFieldName);
		BaseFieldDefinition occupationFieldDefinition = new BaseFieldDefinition(occupationFieldName);
		BaseFieldDefinition ageFieldDefinition = new BaseFieldDefinition(ageFieldName);


		List<BaseFieldDefinition> fieldDefinitions = new ArrayList<BaseFieldDefinition>();
		fieldDefinitions.add(nameFieldDefinition);
		fieldDefinitions.add(occupationFieldDefinition);
		fieldDefinitions.add(ageFieldDefinition);

		
		SimpleDelimitedFlatFileReader reader = new SimpleDelimitedFlatFileReader(rootElementName, fieldDefinitions, true);
		reader.setDelimiter(delimiter);
		
		InputSource inputSource = new InputSource(new ByteArrayInputStream(flatFileContent));
		
		mockery.checking(new Expectations() {
			{
				one(contentHandler).startDocument();
				
				//start the root element
				one(contentHandler).startElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)), (Attributes)with(an(Attributes.class)));

				
				//expect the name
				contentHandlerExpectsTag(mockery, contentHandler, nameFieldName, nameCharacters);

				//expect the occupation tag
				contentHandlerExpectsTag(mockery, contentHandler, occupationFieldName, occupationCharacters);

				//expect the age tag
				contentHandlerExpectsTag(mockery, contentHandler, ageFieldName, ageCharacters);

				//end the root element
				one(contentHandler).endElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)));
				one(contentHandler).endDocument();
			}
		});	
		
		
		reader.setContentHandler(contentHandler);
		reader.parse(inputSource);
		
		mockery.assertIsSatisfied();
	
	}

	/**
	 * Creates the expectation that a tag will be started, populated and ended
	 * 
	 * @param mockery
	 * @param contentHandler
	 * @param tagName
	 * @param tagContent
	 * @throws SAXException
	 */
	static void contentHandlerExpectsTag(Mockery mockery, final ContentHandler contentHandler, final String tagName, final char[] tagContent) throws SAXException{
		mockery.checking(new Expectations() {
			{
				//expect the name
				one(contentHandler).startElement(with(equal("")), with(equal(tagName)), with(equal(tagName)), (Attributes)with(an(Attributes.class)));
				one(contentHandler).characters(tagContent, 0, tagContent.length);
				one(contentHandler).endElement(with(equal("")), with(equal(tagName)), with(equal(tagName)));
			}
		});	
	}
	
	/**
	 * Test the parsing of input source with optional fields
	 * @throws IOException
	 * @throws SAXException
	 */
	@Test
	public void testParseInputSource_withOptionalFields() throws IOException, SAXException {
		final String field1Name = "field1";
		final String field2Name = "field2";
		final String field3Name = "field3";
		final String field4Name = "field4";
		
		final String rootElementName = "rootElementName";
		
		
		BaseFieldDefinition field1FieldDefinition = new BaseFieldDefinition(field1Name);
		BaseFieldDefinition field2FieldDefinition = new BaseFieldDefinition(field2Name);
		BaseFieldDefinition field3FieldDefinition = new BaseFieldDefinition(field3Name);
		field3FieldDefinition.setOptionalField(true);
		BaseFieldDefinition field4FieldDefinition = new BaseFieldDefinition(field4Name);
		
		List<BaseFieldDefinition> fieldDefinitions = new ArrayList<BaseFieldDefinition>();
		fieldDefinitions.add(field1FieldDefinition);
		fieldDefinitions.add(field2FieldDefinition);
		fieldDefinitions.add(field3FieldDefinition);
		fieldDefinitions.add(field4FieldDefinition);
		
			
		BaseStringHandlingFlatFileReader reader = new SimpleDelimitedFlatFileReader(rootElementName, fieldDefinitions, true);
		reader.setContentHandler(contentHandler);
		
		
		
		//test that all fields appear when all are supplied, even tho one is optional
		String testDataWithAllFields = "ab,cd,ef,gh";
		InputSource inputSource = new InputSource(new ByteArrayInputStream(testDataWithAllFields.getBytes()));
		
		mockery.checking(new Expectations() {
			{
				one(contentHandler).startDocument();
				
				//start the root element
				one(contentHandler).startElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)), (Attributes)with(an(Attributes.class)));

				//expect the field1 tag
				contentHandlerExpectsTag(mockery, contentHandler, field1Name, "ab".toCharArray());
				
				//expect the field2 tag
				contentHandlerExpectsTag(mockery, contentHandler, field2Name, "cd".toCharArray());
				
				//expect the field3 tag
				contentHandlerExpectsTag(mockery, contentHandler, field3Name, "ef".toCharArray());
				
				//expect the field4 tag
				contentHandlerExpectsTag(mockery, contentHandler, field4Name, "gh".toCharArray());

				//end the root element
				one(contentHandler).endElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)));

				one(contentHandler).endDocument();
			}
		});	
		

		reader.parse(inputSource);
		mockery.assertIsSatisfied();
		
		//test that all fields appear when all are supplied, even tho one is optional
		String testDataWithFieldsUpToFirstOptionalField = "ab,cd";

		
		mockery.checking(new Expectations() {
			{
				one(contentHandler).startDocument();

				//start the root element
				one(contentHandler).startElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)), (Attributes)with(an(Attributes.class)));

				//expect the field1 tag
				contentHandlerExpectsTag(mockery, contentHandler, field1Name, "ab".toCharArray());
				
				//expect the field2 tag
				contentHandlerExpectsTag(mockery, contentHandler, field2Name, "cd".toCharArray());
				
				//end the root element
				one(contentHandler).endElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)));

				one(contentHandler).endDocument();
			}
		});	
		

		reader.parse(new InputSource(new ByteArrayInputStream(testDataWithFieldsUpToFirstOptionalField.getBytes())));
		mockery.assertIsSatisfied();
		
		//test that it blows up if you include an optional field, but subsequently dont include a following non optional one
		String testDataWithFieldsUpToOptionalFieldButNotBeyond = "ab,cd,ef";

		
		mockery.checking(new Expectations() {
			{
				one(contentHandler).startDocument();

				//start the root element
				one(contentHandler).startElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)), (Attributes)with(an(Attributes.class)));

				//expect the field1 tag
				contentHandlerExpectsTag(mockery, contentHandler, field1Name, "ab".toCharArray());
				
				//expect the field2 tag
				contentHandlerExpectsTag(mockery, contentHandler, field2Name, "cd".toCharArray());
				
				//expect the field3 tag opened 
				contentHandlerExpectsTag(mockery, contentHandler, field3Name, "ef".toCharArray());

				//end the root element
				one(contentHandler).endElement(with(equal("")), with(equal(rootElementName)), with(equal(rootElementName)));

				one(contentHandler).endDocument();
			}
		});	
		
		SAXException saxException = null;
		try{
			reader.parse(new InputSource(new ByteArrayInputStream(testDataWithFieldsUpToOptionalFieldButNotBeyond.getBytes())));
		} catch (SAXException sae){
			saxException=sae;
		}
		Assert.assertNotNull("should have thrown exception for missing field 4", saxException);
		
		
	}
}
