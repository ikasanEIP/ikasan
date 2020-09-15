package org.ikasan.dashboard.ui.administration.schedule;

import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;

import javax.annotation.PostConstruct;
import java.util.List;

public class LdapDirectorySynchronisationFactory {

    private SecurityService securityService;

    private LdapService ldapService;

    @PostConstruct
    public void scheduleJobs() {
        List<AuthenticationMethod> authenticationMethods = this.securityService.getAuthenticationMethods();

        authenticationMethods.forEach(authenticationMethod -> {
            if(authenticationMethod.getIsScheduled()) {

            }
        });
    }
}
