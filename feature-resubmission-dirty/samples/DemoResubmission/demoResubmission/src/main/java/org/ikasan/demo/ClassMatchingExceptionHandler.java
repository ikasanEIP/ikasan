package org.ikasan.demo;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.IkasanExceptionHandler;
import org.ikasan.framework.component.LoggingExceptionHandlerImpl;
import org.ikasan.framework.exception.IkasanExceptionAction;

public class ClassMatchingExceptionHandler extends LoggingExceptionHandlerImpl implements IkasanExceptionHandler{

	private Map<Class<? extends Throwable>, IkasanExceptionAction> errorMappings = new HashMap<Class<? extends Throwable>, IkasanExceptionAction>();

	private Logger logger = Logger.getLogger(ClassMatchingExceptionHandler.class);

	/**
	 * Constructor
	 * 
	 * @param errorMappings
	 */
	public ClassMatchingExceptionHandler(
			Map<Class<? extends Throwable>, IkasanExceptionAction> errorMappings) {
		super();
		this.errorMappings = errorMappings;
	}

	/**
     * Push an exception that occurred whilst handling a data event to the Exception Handler
     * 
     * @param componentName name of the component within which the exception occurred
     * @param event The original event
     * @param throwable The exception
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, final Event event, final Throwable throwable){
    	logger.info("about to check for configured action");
    	
    	IkasanExceptionAction mappedAction = resolveAction(throwable);
    	if (mappedAction!=null){
    		return mappedAction;
    	}
    	logger.info("did not find configured action");
    	
    	return super.invoke(componentName, event, throwable);
    }

    /**
     * Push an exception that occurred outside the scope of handling a data event to the Exception Handler
     * 
     * @param componentName name of the component within which the exception occurred
     * @param throwable The exception
     * @return IkasanExceptionAction
     */
    public IkasanExceptionAction invoke(final String componentName, final Throwable throwable){
    	IkasanExceptionAction mappedAction = resolveAction(throwable);
    	if (mappedAction!=null){
    		return mappedAction;
    	}
    	
    	return super.invoke(componentName, throwable);
    }

	private IkasanExceptionAction resolveAction(Throwable throwable) {
		IkasanExceptionAction result = null;
		
		Class <? extends Throwable> thisThrowableClass = throwable.getClass();
		

		for (Class <? extends Throwable> mappedThrowableClass :   errorMappings.keySet()){
			if (mappedThrowableClass==thisThrowableClass){
				result = errorMappings.get(mappedThrowableClass);
				break;
			}
			
			//TODO iterate up thisThroableClass's heirarchy trying to find a best match
		}
		logger.info("about to return resolved action:"+result);
		
		return result;
	}
	
    /**
     * @return
     */
    public Map<Class<? extends Throwable>, IkasanExceptionAction> getErrorMappings() {
		return errorMappings;
	}
}
