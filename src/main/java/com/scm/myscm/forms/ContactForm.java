package com.scm.myscm.forms;

import com.scm.myscm.validator.ValidFile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ContactForm {

    @NotBlank(message = "Contact Name is Required")
    private String name;

    @Email(message = "Invalid Email Address")
    private String email;

    @NotBlank(message = "Contact Number is Required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Contact Number")
    private String phoneNumber;

    private String address;

    private String description;

    private boolean favorite;

    private String websiteLink;

    private String linkedInLink;

    @ValidFile
    private MultipartFile contactImage;

    private String contactImageUrl;

}
