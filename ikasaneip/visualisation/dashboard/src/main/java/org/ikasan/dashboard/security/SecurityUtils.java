package org.ikasan.dashboard.security;


import com.vaadin.flow.server.ServletHelper.RequestType;
import com.vaadin.flow.shared.ApplicationConstants;
import org.ikasan.security.model.User;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * SecurityUtils takes care of all such static operations that have to do with
 * security and querying rights from different beans of the UI.
 *
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Util methods only
    }

    /**
     * Tests if the request is an internal framework request. The test consists of
     * checking if the request parameter is present and if its value is consistent
     * with any of the request types know.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @return true if is an internal framework request. False otherwise.
     */
    static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Tests if some user is authenticated. As Spring Security always will create an {@link AnonymousAuthenticationToken}
     * we have to ignore those tokens explicitly.
     */
    static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
    }

    /**
     * Get modules available for a given user.
     *
     * @param authentication
     * @return
     */
    public static Set<String> getAccessibleModules(IkasanAuthentication authentication)
    {
        User user = (User)authentication.getPrincipal();

        Set<String> results = new HashSet<>();

        user.getPrincipals().stream()
            .forEach(principal -> principal.getRoles().stream()
                .forEach(role -> role.getRoleModules().stream()
                    .forEach(roleModule -> results.add(roleModule.getModuleName()))));

        return results;
    }
}


