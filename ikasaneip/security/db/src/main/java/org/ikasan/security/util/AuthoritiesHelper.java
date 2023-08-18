package org.ikasan.security.util;

import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.JobPlanGrantedAuthority;
import org.ikasan.security.model.ModuleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AuthoritiesHelper {

    /**
     * Helper method to get all the granted authorities associated with a collection of IkasanPrincipals.
     * 
     * @param ikasanPrincipals
     * @return
     */
    public static List<GrantedAuthority> getGrantedAuthorities(Collection<IkasanPrincipal> ikasanPrincipals) {
        List<GrantedAuthority> authorities = ikasanPrincipals.stream()
            .flatMap(principal -> principal.getRoles().stream())
            .flatMap(r -> r.getPolicies().stream())
            .distinct().
            collect(Collectors.toList());

        authorities.addAll(ikasanPrincipals.stream()
            .flatMap(ikasanPrincipal -> ikasanPrincipal.getRoles().stream())
            .flatMap(role -> role.getRoleModules().stream())
            .map(roleModule -> new ModuleGrantedAuthority(roleModule.getModuleName()))
            .collect(Collectors.toList()));

        authorities.addAll(ikasanPrincipals.stream()
            .flatMap(ikasanPrincipal -> ikasanPrincipal.getRoles().stream())
            .flatMap(role -> role.getRoleJobPlans().stream())
            .map(roleJobPlan -> new JobPlanGrantedAuthority(roleJobPlan.getJobPlanName()))
            .collect(Collectors.toList()));

        return authorities;
    }
}
