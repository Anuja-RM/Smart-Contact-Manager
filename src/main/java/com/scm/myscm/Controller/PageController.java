package com.scm.myscm.Controller;
import com.scm.myscm.entities.User;
import com.scm.myscm.forms.UserForm;
import com.scm.myscm.helpers.Message;
import com.scm.myscm.helpers.MessageType;
import com.scm.myscm.repositories.UserRepo;
import com.scm.myscm.services.UserServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    //use constructor injection instead, for production purpose
    @Autowired
    private UserServices userServices;

    @Autowired
    private UserRepo userRepo;


    @GetMapping("/")
    public String index(){
        return "redirect:/home";
    }

    @RequestMapping("/home")
    public String home() {
        return "home";
    }

    @RequestMapping("/about")
    public String aboutPage() {
        return "about";
    }

    @RequestMapping("/services")
    public String servicesPage() {
        return "services";
    }

    @RequestMapping("/contact")
    public String contactPage() {
        return "contact_us";
    }

    @RequestMapping("/signup")
    public String signupPage(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("userForm", userForm);
        return "signup";
    }

    @RequestMapping("/login")
    public String loginPage() {
        return "login";
    }

    //process signup form
    @RequestMapping(value = "do-signup", method = RequestMethod.POST)
    public String processSignup(@Valid @ModelAttribute UserForm  userForm, BindingResult rBindingResult, HttpSession session) {
        //fetch form data
        //The session can be established throught HttpSession via this we have Removed the message for a session throught an exception in try catch view SS below

        //validate form data, for this add annotations in the userForm that passes the form data to the backend view SS below
        if(rBindingResult.hasErrors()){
            session.setAttribute("message", Message.builder().content("Please correct the following errors").type(MessageType.red).build());
            return "signup";
        }

        //save to database (get the data from form then save)
        /* User user = User.builder()
                .name(userForm.getName())
                .email(userForm.getEmail())
                .password(userForm.getPassword())
                .phoneNumber(userForm.getPhoneNumber())
                .about(userForm.getAbout())
                .profilePic("/images/defaultProfile.jpg")
                .build();
         */
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setAbout(userForm.getAbout());
        user.setProfilePic("/images/defaultProfile.jpg");
        User savedUser = userServices.saveUser(user);

        //message signed up successfully
        Message message = Message.builder().content("Registration Successful").type(MessageType.green).build();
        session.setAttribute("message",message);

        //redirect
        return "redirect:/signup";
    }

    //Process Email verification
    @RequestMapping("/auth/verify-email")
    public String verifyEmail(@RequestParam("token") String token){

        User user = userRepo.findByEmailToken(token).orElse(null);

        if (user != null){
            //user is fetched, process it
            if(user.getEmailToken().equals(token)){
                user.setEmailVerified(true);
                user.setEnabled(true);
                userRepo.save(user);
                return "success_page";
            }

            return "error_page";
        }

        return "error_page";
    }

}
