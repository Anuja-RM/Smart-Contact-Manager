package com.scm.myscm.ServiceImpl;
import com.scm.myscm.entities.User;
import com.scm.myscm.helpers.AppConstants;
import com.scm.myscm.helpers.EmailVerificationLink;
import com.scm.myscm.helpers.ResourseNotFoundException;
import com.scm.myscm.repositories.UserRepo;
import com.scm.myscm.services.EmailService;
import com.scm.myscm.services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    //For enabling/disabling email verification
    @Value("${scm.email.verification.enabled:false}")
    private boolean emailVerificationEnabled;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public User saveUser(User user) {
        //Need to generate email dynamically
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);

        //set password encoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        //set the user role
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        logger.info(user.getProviders().toString());

        if (emailVerificationEnabled) {
            user.setEnabled(false);

            String emailToken = UUID.randomUUID().toString();
            user.setEmailToken(emailToken);

            User savedUser = userRepo.save(user);

            String emailLink = EmailVerificationLink.getLinkForEmailVerification(emailToken);
            emailService.sendEmail(savedUser.getEmail(),
                    "Verify Account : Smart Contact Manager",
                    emailLink);

            return savedUser;
        } else {
            // no verification → enable immediately
            user.setEnabled(true);
            user.setEmailToken(null);

            return userRepo.save(user);
        }
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        //fetch the user from DB (old data)
        User user1 = userRepo.findById(user.getUserId()).orElseThrow(()-> new ResourseNotFoundException("User Not Found"));
        //update the user with new data
        user1.setName(user.getName());
        user1.setEmail(user.getEmail());
        user1.setPassword(user.getPassword());
        user1.setPhoneNumber(user.getPhoneNumber());
        user1.setAbout(user.getAbout());
        user1.setProfilePic(user.getProfilePic());
        user1.setEnabled(user.isEnabled());
        user1.setEmailVerified(user.isEmailVerified());
        user1.setPhoneVerified(user.isPhoneVerified());
        user1.setProviders(user.getProviders());
        user1.setProviderUserId(user.getProviderUserId());

        //save the new data
        User save = userRepo.save(user1);

        //Returns an Optional describing the given value if non-null, otherwise returns an empty Optional.
        return Optional.ofNullable(save);
    }

    @Override
    public void deleteUserById(String id) {
        //fetch the user from DB (old data)
        User user1 = userRepo.findById(id).orElseThrow(()-> new ResourseNotFoundException("User Not Found"));
        userRepo.delete(user1);
    }

    @Override
    public boolean isUserExists(String id) {
        User user1 = userRepo.findById(id).orElse(null);
        return user1!=null ? true : false;
    }

    @Override
    public boolean isUserExistsByEmail(String email) {
        User user1 = userRepo.findByEmail(email).orElse(null);
        return user1!=null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }
}
