package org.ikasan.dashboard.ui.administration.schedule;

import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.LdapServiceException;
import org.ikasan.security.service.SecurityService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class LdapDirectorySynchronisationJob implements Job {

    private static Logger logger = LoggerFactory.getLogger(LdapDirectorySynchronisationJob.class);

    private AuthenticationMethod authenticationMethod;

    private LdapService ldapService;
    private SecurityService securityService;

    public LdapDirectorySynchronisationJob(AuthenticationMethod authenticationMethod, LdapService ldapService,
                                           SecurityService securityService) {
        this.authenticationMethod = authenticationMethod;
        if(this.authenticationMethod == null) {
            throw new IllegalArgumentException("authenticationMethod cannot be null!");
        }
        this.ldapService = ldapService;
        if(this.ldapService == null) {
            throw new IllegalArgumentException("ldapService cannot be null!");
        }
        this.securityService = securityService;
        if(this.securityService == null) {
            throw new IllegalArgumentException("securityService cannot be null!");
        }
    }


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        try {
            this.ldapService.synchronize(authenticationMethod);
            this.authenticationMethod.setLastSynchronised(new Date());
            this.securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
        }
        catch (LdapServiceException e) {
            throw new JobExecutionException(e);
        }
    }

    public String getJobName() {
        return this.authenticationMethod.getName();
    }

    public String getCronExpression() {
        return this.authenticationMethod.getSynchronisationCronExpression();
    }
}
