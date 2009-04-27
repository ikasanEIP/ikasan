/* 
 * $Id: AggregatedContentInputStreamTest.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/test/java/org/ikasan/framework/util/AggregatedContentInputStreamTest.java $
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
package org.ikasan.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for AggregatedContentInputStream
 * 
 * @author Ikasan Development Team
 *
 */
public class AggregatedContentInputStreamTest extends
		AggregatedContentInputStream {

	private static final String HEADER = "Header";
	private static final String FOOTER = "Footer";
	private static final String BEFORE_CONTENT = "BeforeContent";
	private static final String AFTER_CONTENT = "AfterContent";

	private static final String content1 = "content1";
	private static final String content2 = "content2";
	private static final String content3 = "content3";
	
	byte[][] contents =null;
	byte[] footer=null;
	byte[] header=null;
	byte[] beforeContent = null;
	byte[] afterContent = null;

	private int contentPointer = 0;

	@Override
	protected byte[] getAfterContent() {
		return afterContent;
	}

	@Override
	protected byte[] getBeforeContent() {
		return beforeContent;
	}

	@Override
	protected byte[] getFooter() {
		return footer;
	}

	@Override
	protected byte[] getHeader() {
		return header;
	}

	@Override
	protected byte[] getNextContent() {
		return contents[contentPointer++];
	}

	@Override
	protected boolean hasMoreContent() {
		if (contents==null){
			return false;
		}
		if (contentPointer>contents.length-1){
			return false;
		}
		return true;
	}
	
	/**
	 * Sets up some default values
	 */
	@Before
	public void initialise(){
		contentPointer = 0;
		this.contents =  new byte[][]{content1.getBytes(), content2.getBytes(), content3.getBytes()};
		this.header = HEADER.getBytes();
		this.footer = FOOTER.getBytes();
		this.beforeContent = BEFORE_CONTENT.getBytes();
		this.afterContent = AFTER_CONTENT.getBytes();
	}
	

	/**
	 * Tests the case where header, footer, multiple contents, beforeContent and afterContent all exist and are non empty
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithAllValuesPresent() throws IOException {

		byte[] actualContent = inputStreamToBytes(this);
		byte[] expectedContent = (HEADER+BEFORE_CONTENT+content1+AFTER_CONTENT+BEFORE_CONTENT+content2+AFTER_CONTENT+BEFORE_CONTENT+content3+AFTER_CONTENT+FOOTER).getBytes();
		
		Assert.assertTrue(Arrays.equals(actualContent, expectedContent));
		
		
	}
	
	/**
	 * Tests the case where the contents is empty 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithEmptyContent() throws IOException {
		byte[] expectedContent = (HEADER+FOOTER).getBytes();
		
		this.contents =  new byte[][]{};
		Assert.assertEquals(new String(expectedContent),new String(inputStreamToBytes(this)), new String(expectedContent));
		
	}
	
	/**
	 * Tests the case where the contents is null
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithNullOrEmptyContent() throws IOException {
		byte[] expectedContent = (HEADER+FOOTER).getBytes();
		
		this.contents =  null;
		Assert.assertEquals(new String(expectedContent),new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	

	
	/**
	 * Tests the case where the header is empty 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithEmptyHeader() throws IOException {
		byte[] expectedContent = (BEFORE_CONTENT+content1+AFTER_CONTENT+BEFORE_CONTENT+content2+AFTER_CONTENT+BEFORE_CONTENT+content3+AFTER_CONTENT+FOOTER).getBytes();

		this.header =  new byte[]{};
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	
	/**
	 * Tests the case where the header is null;
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithNullHeader() throws IOException {
		byte[] expectedContent = (BEFORE_CONTENT+content1+AFTER_CONTENT+BEFORE_CONTENT+content2+AFTER_CONTENT+BEFORE_CONTENT+content3+AFTER_CONTENT+FOOTER).getBytes();
		
		this.header =  null;
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	
	
	
	/**
	 * Tests the case where the before content is empty
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithEmptyBeforeContent() throws IOException {
		byte[] expectedContent = (HEADER+content1+AFTER_CONTENT+content2+AFTER_CONTENT+content3+AFTER_CONTENT+FOOTER).getBytes();
		
		this.beforeContent =  new byte[]{};
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	
	/**
	 * Tests the case where the before content is null
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithNullOrEmptyBeforeContent() throws IOException {
		byte[] expectedContent = (HEADER+content1+AFTER_CONTENT+content2+AFTER_CONTENT+content3+AFTER_CONTENT+FOOTER).getBytes();

		this.beforeContent =  null;
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	

	
	/**
	 * Test the case where the afterContent is null 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithNullAfterContent() throws IOException {
		byte[] expectedContent = (HEADER+BEFORE_CONTENT+content1+BEFORE_CONTENT+content2+BEFORE_CONTENT+content3+FOOTER).getBytes();

		this.afterContent =  null;
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
		

	}
	
	/**
	 * Test the case where the afterContent is empty
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithEmptyAfterContent() throws IOException {
		byte[] expectedContent = (HEADER+BEFORE_CONTENT+content1+BEFORE_CONTENT+content2+BEFORE_CONTENT+content3+FOOTER).getBytes();

		this.afterContent =  new byte[]{};
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	

	/**
	 * Test the case where the footer is null 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithNullFooter() throws IOException {
		byte[] expectedContent = (HEADER+BEFORE_CONTENT+content1+AFTER_CONTENT+BEFORE_CONTENT+content2+AFTER_CONTENT+BEFORE_CONTENT+content3+AFTER_CONTENT).getBytes();

		this.footer =  null;
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	
	/**
	 * Test the case where the footer is empty 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testReadWithEmptyFooter() throws IOException {
		byte[] expectedContent = (HEADER+BEFORE_CONTENT+content1+AFTER_CONTENT+BEFORE_CONTENT+content2+AFTER_CONTENT+BEFORE_CONTENT+content3+AFTER_CONTENT).getBytes();
		
		this.footer =  new byte[]{};
		Assert.assertEquals(new String(inputStreamToBytes(this)), new String(expectedContent));
	}
	

	
	static byte[] inputStreamToBytes(InputStream in) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0){
			out.write(buffer, 0, len);
		}

		in.close();
		out.close();
		byte[] result = out.toByteArray();
		return result;
	}

}
