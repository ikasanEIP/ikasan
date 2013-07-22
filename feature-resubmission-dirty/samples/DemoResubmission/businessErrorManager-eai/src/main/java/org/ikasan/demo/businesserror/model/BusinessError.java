package org.ikasan.demo.businesserror.model;

import org.apache.log4j.Logger;

public class BusinessError {
	
	private Long id;
	
	private String errorMessage;
	
	private String errorSummary;

	private String originatingSystem;
	
	private boolean resubmittable = true;
	
	private static Logger logger = Logger.getLogger(BusinessError.class);
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	private String externalReference;

	public BusinessError(String originatingSystem, String externalReference, String errorMessage) {
		this.originatingSystem = originatingSystem;
		this.externalReference = externalReference;
		this.errorMessage = errorMessage;
		
		String firstLine = errorMessage;
		int endOfFirstLine = errorMessage.indexOf("\n");
		if (endOfFirstLine>-1){
			firstLine = errorMessage.substring(0, endOfFirstLine);
		}
		this.errorSummary = firstLine;
		if (errorSummary.length()>80){
			errorSummary = errorSummary.substring(0, 77)+"...";
		}
		
	}

	public String getExternalReference() {
		return externalReference;
	}
	
	public String getErrorSummary(){
		return errorSummary;
	}
	

	
	public void setOriginatingSystem(String originatingSystem){
		this.originatingSystem = originatingSystem;
	}
	
	public String getOriginatingSystem(){
		return originatingSystem;
	}

	@Override
	public String toString() {
		return "BusinessError ["
				+ ", errorSummary=" + errorSummary + ", externalReference="
				+ externalReference + ", id=" + id + "]";
	}
	
	public boolean isResubmittable(){
		return resubmittable;
	}




}
