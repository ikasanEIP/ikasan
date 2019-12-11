package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.Component;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ComponentSecurityVisibility
{
    public static void applySecurity(Component component, String ... securityConstants)
    {
        if(component == null)
        {
            return;
        }

        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        component.setVisible(false);

        if(authentication == null)
        {
            return;
        }

        for(String securityConstant: securityConstants)
        {
            if(authentication.hasGrantedAuthority(securityConstant))
            {
                component.setVisible(true);
            }
        }
    }

    public static boolean hasAuthorisation(String ... securityConstants)
    {
        if(SecurityContextHolder.getContext().getAuthentication() instanceof  IkasanAuthentication)
        {
            IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

            for (String securityConstant : securityConstants)
            {
                if (authentication.hasGrantedAuthority(securityConstant))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
