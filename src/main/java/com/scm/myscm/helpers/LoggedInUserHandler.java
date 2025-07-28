package com.scm.myscm.helpers;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class LoggedInUserHandler {

    public static String getEmailOfLoggedInUser(Authentication authentication){

        //if user is LoggedIn via email & password || Google || GitHub then how to fetch email
        if(authentication instanceof OAuth2AuthenticationToken){

            var OAuth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
            var ClientId = OAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
            var OAuth2User = (OAuth2User)authentication.getPrincipal();
            String username = "";

            //Via Google
            if(ClientId.equalsIgnoreCase("google")){
                username = OAuth2User.getAttribute("email");
            }
            else if(ClientId.equalsIgnoreCase("github")){
                username = OAuth2User.getAttribute("email") != null ? OAuth2User.getAttribute("email").toString()
                        : OAuth2User.getAttribute("login").toString()+"@gmail.com";
            }
            return username;

        }
        else{
            return authentication.getName();
        }

    }

}