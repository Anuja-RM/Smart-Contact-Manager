package com.scm.myscm.Controller;

import com.scm.myscm.entities.User;
import com.scm.myscm.helpers.LoggedInUserHandler;
import com.scm.myscm.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class RootController {

    @Autowired
    private UserServices userService;

    @ModelAttribute
    public void addLoggedInUserInformation(Model model, Authentication authentication) {

        if (authentication == null) {
            return;
        }

        System.out.println("Adding Logged in user infomation");
        String username = LoggedInUserHandler.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        model.addAttribute("LoggedInUser",user);
    }

}