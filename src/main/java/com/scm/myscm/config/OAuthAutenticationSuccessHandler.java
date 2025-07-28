package com.scm.myscm.config;

import com.scm.myscm.entities.Providers;
import com.scm.myscm.entities.User;
import com.scm.myscm.helpers.AppConstants;
import com.scm.myscm.repositories.UserRepo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class OAuthAutenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepo userRepo;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //Typecasting general authentication to OAuth Authentication
        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        //Fetching ClientRegistrationId so that we can get attributes according to the client (Google, GitHub or simple Login)
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        //Typecasting normal user to oauthUser
        var oauthUser = (DefaultOAuth2User)authentication.getPrincipal();

        //creating a user to save into database(some general user setting to be saved by default)
        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        user.setEmailVerified(true);
        user.setEnabled(true);
        user.setPassword("password");

        //user settings according to the client authentication (google or github)
        if(authorizedClientRegistrationId.equalsIgnoreCase("google")){
            //setting user data for google
            user.setEmail(oauthUser.getAttribute("email").toString());
            user.setProfilePic(oauthUser.getAttribute("picture").toString());
            user.setName(oauthUser.getAttribute("name").toString());
            user.setProviders(Providers.GOOGLE);
            user.setProviderUserId(oauthUser.getName());
            user.setAbout("This User is created via Google");

        }
        else if (authorizedClientRegistrationId.equalsIgnoreCase("github")) {
            //fetching data first for github as some attributes are different not as default
            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email").toString()
                    : oauthUser.getAttribute("login").toString()+"@gmail.com";
            String picture = oauthUser.getAttribute("avatar_url").toString();
            String name = oauthUser.getAttribute("login").toString();
            String providerUserId = oauthUser.getName();
            //setting user data for github
            user.setEmail(email);
            user.setProfilePic(picture);
            user.setName(name);
            user.setProviders(Providers.GITHUB);
            user.setProviderUserId(providerUserId);
            user.setAbout("This User is created via Github");

        }
        else {
            System.out.println("Unknown Provider");
        }



        /*
        //This authentication was only for google

        DefaultOAuth2User user = (DefaultOAuth2User) authentication.getPrincipal();

        //get data for database
        String email = user.getAttribute("email").toString();
        String name = user.getAttribute("name").toString();
        String picture = user.getAttribute("picture").toString();

        //create user and save in database
        User user1 = new User();
        user1.setEmail(email);
        user1.setName(name);
        user1.setProfilePic(picture);
        user1.setPassword("password");
        user1.setUserId(UUID.randomUUID().toString());
        user1.setProviders(Providers.GOOGLE);
        user1.setEnabled(true);
        user1.setEmailVerified(true);
        user1.setProviderUserId(user.getName());
        user1.setRoleList(List.of(AppConstants.ROLE_USER));
        user1.setAbout("This is the account created by Google user");

         */

        //save the data into database
        User user2 = userRepo.findByEmail(user.getEmail()).orElse(null);
        if(user2 == null){
            userRepo.save(user);
        }

        //after saving redirect to user dashboard
        new DefaultRedirectStrategy().sendRedirect(request,response,"/user/profile");

    }
}
