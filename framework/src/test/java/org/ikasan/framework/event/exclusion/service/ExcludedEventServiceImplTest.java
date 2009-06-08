/**
 * 
 */
package org.ikasan.framework.event.exclusion.service;

import java.util.ArrayList;
import java.util.List;

import org.ikasan.framework.component.Event;
import org.ikasan.framework.event.exclusion.dao.ExcludedEventDao;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;


/**
 * Test class for ExcludedEventServiceImpl
 * 
 * 
 * @author The Ikasan Development Team
 *
 */
public class ExcludedEventServiceImplTest {
	
	/**
	 * Mockery for testing
	 */
	private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    private Event excludedEvent = mockery.mock(Event.class);
    
    private ExcludedEventDao excludedEventDao = mockery.mock(ExcludedEventDao.class);
    
    private ExcludedEventListener excludedEventListener1 = mockery.mock(ExcludedEventListener.class, "excludedEventListener1");
    
    private ExcludedEventListener excludedEventListener2 = mockery.mock(ExcludedEventListener.class, "excludedEventListener2");
	
	/**
	 * Class under test
	 */
	private ExcludedEventServiceImpl excludedEventService;
    
	/**
	 * Constructor
	 */
	public ExcludedEventServiceImplTest(){
		List<ExcludedEventListener> listeners = new ArrayList<ExcludedEventListener>();
		listeners.add(excludedEventListener1);
		listeners.add(excludedEventListener2);
		excludedEventService = new ExcludedEventServiceImpl(excludedEventDao, listeners);
	}
	
	@Test
	public void testExcludeEvent(){
		final Sequence sequence = mockery.sequence("invocationSequence");
		
		mockery.checking(new Expectations()
        {
            {
            	one(excludedEventListener1).notifyExcludedEvent(excludedEvent);
            	inSequence(sequence);
            	one(excludedEventListener2).notifyExcludedEvent(excludedEvent);
            	inSequence(sequence);
            }
        });
		
		excludedEventService.excludeEvent(excludedEvent);
		
		mockery.assertIsSatisfied();
	}
	

}
