package com.scm.myscm.config;

import com.scm.myscm.ServiceImpl.SecurityCustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //create user and login using java code with memory service(This is hard coded we need more dynamic way using database data for login)
    //    @Bean
    //    public UserDetailsService userDetailsService() {
    //
    //        UserDetails user1 = User.withDefaultPasswordEncoder().username("admin123").password("admin123").build();
    //
    //        UserDetails user2 = User.withDefaultPasswordEncoder().username("user123").password("user123").build();
    //
    //        var inMemoryUserDetailsService = new InMemoryUserDetailsManager(user1,user2);
    //        return inMemoryUserDetailsService;
    //    }

    @Autowired
    private SecurityCustomUserDetailService userDetailService;
    @Autowired
    private OAuthAutenticationSuccessHandler handler;
    @Autowired
    private AuthFailureHandler authFailureHandler;

    //Configuration for spring security authentication provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        //user detail service's object is to be provided here
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        //password encoder's object is to be provided here
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;

    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        //Configuration of urls (public/private)
        httpSecurity.authorizeHttpRequests(authorize ->{
           //authorize.requestMatchers("/home","/signup","/services","/about","/contact","/login").permitAll();
            authorize.requestMatchers("/user/**").authenticated();
            authorize.anyRequest().permitAll();
        });

        //custom form login configuration
        httpSecurity.formLogin(formLogin ->{
            //Our Custom login page
            formLogin.loginPage("/login");
            formLogin.loginProcessingUrl("/do-login");
            formLogin.defaultSuccessUrl("/user/profile");
            formLogin.failureUrl("/login?error=true");
            formLogin.usernameParameter("email");
            formLogin.passwordParameter("password");
            formLogin.failureHandler(authFailureHandler);
//            Can be used for handling failure or success in login similarly(was just a demo no need of it right now)
//            formLogin.successHandler(new AuthenticationSuccessHandler() {
//                @Override
//                public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//                    throw new UnsupportedOperationException("Not supported yet.");
//                }
//            });

        });

        //logout configuration
        httpSecurity.logout(logout ->{
            logout.logoutUrl("/logout");
            logout.logoutSuccessUrl("/login?logout=true");
        });

        //oauth configurations
        //Google
        httpSecurity.oauth2Login(oauth-> {
            oauth.loginPage("/login");
            oauth.successHandler(handler);
        });

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
