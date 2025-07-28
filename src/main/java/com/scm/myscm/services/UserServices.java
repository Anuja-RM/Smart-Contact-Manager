package com.scm.myscm.services;

import com.scm.myscm.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserServices {

    User saveUser(User user);
    Optional getUserById(String id);
    Optional updateUser(User user);
    void deleteUserById(String id);
    boolean isUserExists(String id);
    boolean isUserExistsByEmail(String email);
    List<User>  getAllUsers();
    User getUserByEmail(String email);

}
