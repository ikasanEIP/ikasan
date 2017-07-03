package org.ikasan.tools.errormanager.eai;


import org.ikasan.tools.errormanager.model.BusinessError;
import org.junit.Test;
import org.junit.Assert;


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
