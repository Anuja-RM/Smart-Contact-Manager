package com.scm.myscm.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private String contactImage;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private boolean favorite=false;
    private String websiteLink;
    private String linkedInLink;
    private String cloudinaryImagePublicId;
    @ManyToOne
    @JsonIgnoreProperties("contacts")
    private User user;
    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.EAGER,  orphanRemoval = true)
    private List<SocialLink> lists  = new ArrayList<>();

}
