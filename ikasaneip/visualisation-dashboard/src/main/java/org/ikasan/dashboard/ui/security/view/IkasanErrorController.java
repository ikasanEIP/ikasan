package org.ikasan.dashboard.ui.security.view;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IkasanErrorController implements ErrorController
{
 
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        //do something like logging
        return "login";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}