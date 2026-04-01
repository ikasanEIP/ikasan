package org.ikasan.security.util;

import org.ikasan.security.model.JobPlanGrantedAuthorityImpl;
import org.ikasan.security.model.ModuleGrantedAuthorityImpl;
import org.ikasan.spec.security.model.IkasanPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AuthoritiesHelper {


    /**
     * Extracts a list of granted authorities based on the roles, policies, role modules,
     * and role job plans associated with the given Ikasan principals.
     *
     * @param ikasanPrincipals a collection of {@link IkasanPrincipal} instances, each representing
     *                         a security principal. These principals can have associated roles,
     *                         which in turn have policies, role modules, and role job plans.
     * @return a list of {@link GrantedAuthority} objects derived from the roles, policies,
     *         role modules, and role job plans of the given principals. The list may contain
     *         authorities representing policies, modules, and job plans and may include duplicates.
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
            .map(roleModule -> new ModuleGrantedAuthorityImpl(roleModule.getModuleName()))
            .toList());

        authorities.addAll(ikasanPrincipals.stream()
            .flatMap(ikasanPrincipal -> ikasanPrincipal.getRoles().stream())
            .flatMap(role -> role.getRoleJobPlans().stream())
            .map(roleJobPlan -> new JobPlanGrantedAuthorityImpl(roleJobPlan.getJobPlanName()))
            .toList());

        return authorities;
    }
}
