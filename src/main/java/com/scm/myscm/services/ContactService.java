package com.scm.myscm.services;

import com.scm.myscm.entities.Contact;
import com.scm.myscm.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContactService {

    Contact save(Contact  contact);
    Contact update(Contact contact);
    List<Contact> getAll();
    Contact getById(String id);
    void deleteById(String id);
    Page<Contact> searchByEmail(String emailKeyword, int page, int size, String sortBy, String sortDirection, User user);
    Page<Contact> searchByName(String nameKeyword, int page, int size, String sortBy, String sortDirection, User user);
    Page<Contact> searchByPhoneNumber(String phoneKeyword, int page, int size, String sortBy, String sortDirection, User user);
    List<Contact> getByUserId(String userId);
    Page<Contact> getByUser(User user, int page, int size, String sortField, String sortDirection);
    List<Contact> getFavoriteContacts(User user);
}
