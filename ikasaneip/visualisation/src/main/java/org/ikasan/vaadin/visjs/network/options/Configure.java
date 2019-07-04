package org.ikasan.vaadin.visjs.network.options;

/**
 * Created by Martin Prause 4.8.2017
 */
public class Configure {

	 private boolean enabled= false;
	 private boolean showButton= false;
	 private String filter;
	 private String container;
	

	 public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isShowButton() {
		return showButton;
	}
	public void setShowButton(boolean showButton) {
		this.showButton = showButton;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	public String getContainer() {
		return container;
	}
	public void setContainer(String container) {
		this.container = container;
	}
	 
	
}
