package com.scm.myscm.forms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserForm {
    @NotBlank(message = "User is Required")
    @Size(min = 3, message = "Minimum 3 characters are required")
    private String name;

    @NotBlank(message = "Email is Required")
    @Email(message = "Invalid Email Address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password should be of minimum 6 characters")
    private String password;

    @NotBlank(message = "Contact Number is required")
    @Size(min = 10, max = 12, message = "Invalid Phone Number")
    private String phoneNumber;

    @NotBlank(message = "About is required")
    private String about;

}
