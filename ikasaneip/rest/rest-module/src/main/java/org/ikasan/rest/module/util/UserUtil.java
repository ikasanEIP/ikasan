package org.ikasan.rest.module.util;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil
{
    public static String getUser()
    {
        String user = "unknown";
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null)
        {
            user = context.getAuthentication().getPrincipal().toString();
        }
        return user;
    }

}
