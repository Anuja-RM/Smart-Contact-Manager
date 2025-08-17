package com.scm.myscm.Controller;
import com.scm.myscm.entities.Contact;
import com.scm.myscm.entities.User;
import com.scm.myscm.forms.ContactForm;
import com.scm.myscm.forms.ContactSearchForm;
import com.scm.myscm.forms.UserForm;
import com.scm.myscm.helpers.*;
import com.scm.myscm.repositories.UserRepo;
import com.scm.myscm.services.ContactService;
import com.scm.myscm.services.ImageService;
import com.scm.myscm.services.UserServices;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserServices userService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepo userRepo;

    //user dashboard page
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard(){
        return "user/dashboard";
    }

    //user profile page
    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String userProfile(Model model, Authentication authentication){
        return "user/profile";
    }

    //user add contacts page
    @RequestMapping("/contacts/add")
    public String addContactsView(Model model){
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "user/add_contacts";
    }

    //user save contact view
    @RequestMapping(value = "/contacts/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm , BindingResult result, Authentication authentication, HttpSession session){

        if(result.hasErrors()){
            session.setAttribute("message", Message.builder().content("Please correct the following errors").type(MessageType.red).build());
            return "user/add_contacts";
        }

        String username = LoggedInUserHandler.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Contact contact = new Contact();
        contact.setUser(user);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setFavorite(contactForm.isFavorite());
        contact.setDescription(contactForm.getDescription());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        //process contactImage
        if(contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()){
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setContactImage(fileURL);
            contact.setCloudinaryImagePublicId(filename);
        }
        contactService.save(contact);

        session.setAttribute("message", Message.builder().content("You have successfully added a Contact").type(MessageType.green).build());
        return "redirect:/user/contacts/add";
    }

    //user view contacts page
    @RequestMapping("/contacts")
    public String viewContact(
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value="size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value="sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value="direction", defaultValue = "asc") String direction,
            Model model, Authentication authentication){

        String username =   LoggedInUserHandler.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(username);
        Page<Contact> pageContact = contactService.getByUser(user,page,size,sortBy,direction);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("contactSearchForm", new ContactSearchForm());
        return "user/contacts";
    }

    //user search contact view
    @RequestMapping("/contacts/search")
    public String searchHandler(@ModelAttribute ContactSearchForm contactSearchForm,
                                @RequestParam(value="page", defaultValue = "0") int page,
                                @RequestParam(value="size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
                                @RequestParam(value="sortBy", defaultValue = "name") String sortBy,
                                @RequestParam(value="direction", defaultValue = "asc") String direction, Model model,
                                Authentication authentication){


        var user = userService.getUserByEmail(LoggedInUserHandler.getEmailOfLoggedInUser(authentication));


        Page<Contact> pageContact = null;
        if(contactSearchForm.getField().equalsIgnoreCase("name")){
            pageContact = contactService.searchByName(contactSearchForm.getKeyword(), page, size, sortBy, direction, user);
        }
        else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getKeyword(), page, size, sortBy, direction, user);
        }
        else if (contactSearchForm.getField().equalsIgnoreCase("phoneNumber")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getKeyword(), page, size, sortBy, direction, user);
        }

        model.addAttribute("contactSearchForm", contactSearchForm);
        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        return "user/search";
    }


    //user delete contact page
    @RequestMapping("/contacts/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId){

        contactService.deleteById(contactId);
        return "redirect:/user/contacts";
    }

    //user edit contact page
    @RequestMapping("/contacts/edit/{contactId}")
    public String updateContact(@PathVariable("contactId") String contactId,Model model){

        var contact =  contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setContactImageUrl(contact.getContactImage());
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);

        return "user/update_contact";
    }

    @RequestMapping(value = "/contacts/update/{contactId}", method = RequestMethod.POST)
    public String updateContactForm(@PathVariable("contactId") String contactId,@Valid @ModelAttribute ContactForm contactForm,BindingResult bindingResult,Model model){

        if(bindingResult.hasErrors()){
            return "user/update_contact";
        }

        var contact1 = contactService.getById(contactId);
        contact1.setId(contactId);
        contact1.setName(contactForm.getName());
        contact1.setEmail(contactForm.getEmail());
        contact1.setPhoneNumber(contactForm.getPhoneNumber());
        contact1.setAddress(contactForm.getAddress());
        contact1.setDescription(contactForm.getDescription());
        contact1.setFavorite(contactForm.isFavorite());
        contact1.setLinkedInLink(contactForm.getLinkedInLink());
        contact1.setWebsiteLink(contactForm.getWebsiteLink());

        if(contactForm.getContactImage()!=null && !contactForm.getContactImage().isEmpty()){
            String filename = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact1.setCloudinaryImagePublicId(filename);
            contact1.setContactImage(imageUrl);
            //contactForm.setContactImage(imageUrl);
        }

        var updatedContact = contactService.update(contact1);
        model.addAttribute("updatedContact", updatedContact);

        return "redirect:/user/contacts";
    }

    //update/edit user profile
    @RequestMapping("/edit/{id}")
    public String editProfileForm(@PathVariable String id, Model model) {
        User userForm = userRepo.findById(id).orElseThrow(() -> new ResourseNotFoundException("User not Found by "+ id));
        model.addAttribute("userForm", userForm);
        model.addAttribute("userId", id);
        return "user/update_profile";
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public String updateProfile(@PathVariable String id, @Valid @ModelAttribute UserForm userForm, BindingResult rBindingResult, HttpSession session) {

        if(rBindingResult.hasErrors()){
            session.setAttribute("message", Message.builder().content("Please correct the following errors").type(MessageType.red).build());
            return "user/update_profile";
        }

        User existingUser = userRepo.findById(id).orElseThrow(() -> new ResourseNotFoundException("User not Found by " + id));
        existingUser.setName(userForm.getName());
        existingUser.setEmail(userForm.getEmail());
        existingUser.setPhoneNumber(userForm.getPhoneNumber());
        existingUser.setPassword(userForm.getPassword());
        existingUser.setAbout(userForm.getAbout());

        if(userForm.getProfilePic()!=null && !userForm.getProfilePic().isEmpty()){
            String filename = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(userForm.getProfilePic(), filename);
            existingUser.setProfilePic(imageUrl);
        }

        userRepo.save(existingUser);
        return "redirect:/user/profile";
    }


    //view favorite contact
    @GetMapping("/favorites")
    public String viewFavourites(Model model, Authentication authentication) {
        String email = LoggedInUserHandler.getEmailOfLoggedInUser(authentication);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourseNotFoundException("User not found: " + email));

        List<Contact> favorites = contactService.getFavoriteContacts(user);
        model.addAttribute("favorites", favorites);

        return "user/favorites";
    }

}
