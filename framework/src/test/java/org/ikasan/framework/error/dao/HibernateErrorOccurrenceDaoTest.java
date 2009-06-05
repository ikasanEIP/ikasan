/**
 * 
 */
package org.ikasan.framework.error.dao;

import java.sql.CallableStatement;
import java.sql.Connection;

import javax.sql.DataSource;

import junit.framework.Assert;

import org.ikasan.framework.error.model.ErrorOccurrence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Ikasan Development Team
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)

@ContextConfiguration
public class HibernateErrorOccurrenceDaoTest implements InitializingBean{

	
	/**
	 * System under test
	 */
	@Autowired
	private HibernateErrorOccurrenceDao errorOccurrenceDao;
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private String tableCreationQuery;
	
	/**
	 * Test method for {@link org.ikasan.framework.error.dao.HibernateErrorOccurrenceDao#save(org.ikasan.framework.error.model.ErrorOccurrence)}.
	 */
	@Test
	public void testSaveAndLoad() {
		Throwable throwable = new RuntimeException();
		String moduleName = "moduleName";
		String initiatorName = "initiatorName";
		
		
		ErrorOccurrence errorOccurrence = new ErrorOccurrence(throwable, moduleName, initiatorName, null);
		errorOccurrenceDao.save(errorOccurrence);
		
		Long id = errorOccurrence.getId();
		Assert.assertNotNull("id should not be null following save", id);
		
		
		ErrorOccurrence reloaded = errorOccurrenceDao.getErrorOccurrence(id);
		System.out.println("original:"+errorOccurrence);
		System.out.println("reloaded:"+reloaded);
		
		
		Assert.assertTrue("reloaded should be the same as orignal",errorOccurrence.getId().equals(reloaded.getId()));
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		Connection connection = null;
		try{
			connection = dataSource.getConnection("sa", "");
			CallableStatement statement = connection.prepareCall(tableCreationQuery);
			statement.execute();
		
		} finally{
			if (connection!=null){
				if (!connection.isClosed()){
					connection.close();
				}
			}
		}
		
	}

}
