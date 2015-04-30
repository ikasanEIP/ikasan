package org.ikasan.dashboard.ui.framework.web;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

/**
 * 
 * @author CMI2 Development Team
 *
 */
public class AmsOpenSessionInViewFilter extends OpenSessionInViewFilter {
    private Logger logger = Logger.getLogger(AmsOpenSessionInViewFilter.class);

}
