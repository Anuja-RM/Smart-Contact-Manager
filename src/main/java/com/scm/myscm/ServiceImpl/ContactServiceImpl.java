package com.scm.myscm.ServiceImpl;

import com.scm.myscm.entities.Contact;
import com.scm.myscm.entities.User;
import com.scm.myscm.helpers.ResourseNotFoundException;
import com.scm.myscm.repositories.ContactRepo;
import com.scm.myscm.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepo contactRepo;

    @Override
    public Contact save(Contact contact) {
        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);
        return contactRepo.save(contact);
    }

    @Override
    public Contact update(Contact contact) {

        var contactOld = contactRepo.findById(contact.getId()).orElseThrow(()-> new ResourseNotFoundException("Contact not Found"));
        contactOld.setName(contact.getName());
        contactOld.setEmail(contact.getEmail());
        contactOld.setPhoneNumber(contact.getPhoneNumber());
        contactOld.setAddress(contact.getAddress());
        contactOld.setDescription(contact.getDescription());
        contactOld.setWebsiteLink(contact.getWebsiteLink());
        contactOld.setLinkedInLink(contact.getLinkedInLink());
        contactOld.setFavorite(contact.isFavorite());
        contactOld.setContactImage(contact.getContactImage());
        contactOld.setCloudinaryImagePublicId(contact.getCloudinaryImagePublicId());

        return contactRepo.save(contactOld);
    }

    @Override
    public List<Contact> getAll() {
        return contactRepo.findAll();
    }

    @Override
    public Contact getById(String id) {
        return contactRepo.findById(id).orElseThrow(() -> new ResourseNotFoundException("Contact Not Found with id "+id));
    }

    @Override
    public void deleteById(String id) {
        var contact = contactRepo.findById(id).orElseThrow(() -> new ResourseNotFoundException("Contact Not Found with id "+id));
        contactRepo.delete(contact);
    }

    @Override
    public Page<Contact> searchByName(String nameKeyword, int page, int size, String sortBy, String sortDirection, User user) {

        Sort sort = sortDirection.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page,size,sort);
        return contactRepo.findByUserAndNameContaining(user,nameKeyword, pageable);
    }

    @Override
    public Page<Contact> searchByEmail(String emailKeyword, int page, int size, String sortBy, String sortDirection, User user) {

        Sort sort = sortDirection.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page,size,sort);
        return contactRepo.findByUserAndEmailContaining(user,emailKeyword,  pageable);
    }

    @Override
    public Page<Contact> searchByPhoneNumber(String phoneKeyword, int page, int size, String sortBy, String sortDirection, User user) {

        Sort sort = sortDirection.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page,size,sort);
        System.out.println("Searching phone: " + phoneKeyword);
        return contactRepo.findByUserAndPhoneNumberContaining(user,phoneKeyword,  pageable);
    }

    @Override
    public List<Contact> getByUserId(String userId) {
        return contactRepo.findByUserId(userId);
    }

    @Override
    public Page<Contact> getByUser(User user, int page, int size, String sortBy, String sortDirection) {

        Sort sort = sortDirection.equals("desc")? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        var pageable = PageRequest.of(page, size, sort);

        return contactRepo.findByUser(user,pageable);
    }

    @Override
    public List<Contact> getFavoriteContacts(User user) {
        return contactRepo.findByUserAndFavoriteTrue(user);
    }
}
