package com.imgprocessor.request;


import com.imgprocessor.util.Gender;
import com.imgprocessor.util.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank
    @Size(min = 3, max = 20)
    private String userName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;


    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    private String dateOfBirth;
    private Gender gender;
    private UserType userType;

    public SignUpRequest() {
    }
}
