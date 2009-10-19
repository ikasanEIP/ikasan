package org.ikasan.demo.businesserror.eai;

import junit.framework.Assert;

import org.ikasan.demo.businesserror.model.BusinessError;
import org.junit.Test;


public class BusinessErrorConverterTest {

	BusinessErrorConverter businessErrorConverter = new BusinessErrorConverter();
	
	String xml = 
			"<businessError>\n" +
			"  <errorMessage>message</errorMessage>\n" +
			"  <originatingSystem>originatingSystem</originatingSystem>\n" +
			"  <resubmittable>true</resubmittable>\n" +
			"  <externalReference>externalReference</externalReference>\n" +
			"</businessError>";
	
	
	@Test
	public void testToXml(){
		BusinessError businessError = new BusinessError("originatingSystem", "externalReference", "message");
		Assert.assertEquals(xml, businessErrorConverter.toXml(businessError));
	}
}
