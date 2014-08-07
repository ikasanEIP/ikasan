package org.ikasan.component.endpoint.mongo.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class EmbeddedMongoTest {

	@Test
	public void testEmbeddedMongoWithSystemProperty() {
		System.setProperty("ikasan.localMongoDistDirProperty", "myLocalInstallationDir");
		EmbeddedMongo em = new EmbeddedMongo(100);
		assertEquals(100, em.getPort());
		assertEquals("myLocalInstallationDir", em.getMongoDistributionDirectory());
	}

	@Test
	public void testEmbeddedMongoWithNoSystemProperty() {
		System.clearProperty("ikasan.localMongoDistDirProperty");
		EmbeddedMongo em = new EmbeddedMongo(100);
		assertEquals(100, em.getPort());
		assertNull(em.getMongoDistributionDirectory());
	}
	
	@Test
	public void testEmbeddedMongoWithNullLocalDistributionDirector(){
		EmbeddedMongo em = new EmbeddedMongo(100, null);
		assertEquals(100, em.getPort());
		assertNull(em.getMongoDistributionDirectory());
		
	}
	

}
