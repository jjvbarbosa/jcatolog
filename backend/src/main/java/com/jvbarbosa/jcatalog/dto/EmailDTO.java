package com.jvbarbosa.jcatalog.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailDTO {

    @Email(message = "Invalid email")
    @NotBlank(message = "Required field")
    private String email;

    @JsonCreator
    public EmailDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
