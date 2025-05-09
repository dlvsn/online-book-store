package org.example.onlinebookstore.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.example.onlinebookstore.validator.user.FieldMatch;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldMatch.List({
        @FieldMatch(first = "password", second = "repeatPassword", message = "password don't match")
})
public class RegisterUserRequestDto {
    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank
    @Length(min = 6, max = 20,
                    message = "password must contain between 6 and 20 characters")
    private String password;

    @NotBlank(message = "Please, repeat password")
    private String repeatPassword;

    @NotBlank(message = "Please, add first name")
    private String firstName;

    @NotBlank(message = "Please, add last name")
    private String lastName;

    private String shippingAddress;
}
