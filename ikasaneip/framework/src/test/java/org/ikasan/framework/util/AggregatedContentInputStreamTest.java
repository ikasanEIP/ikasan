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
