package org.ikasan.framework.error.serialisation;

import java.util.Date;

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.junit.Test;

public class ErrorOccurrenceXmlSerialiserTest {

	private ErrorOccurrenceXmlSerialiser errorOccurrenceXmlSerialiser = new ErrorOccurrenceXmlSerialiser();
	@Test
	public void testToXml() {
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(new RuntimeException(), "moduleName", "initiatorName", new Date());
		
		System.out.println(errorOccurrenceXmlSerialiser.toXml(errorOccurrence));
	}

}
