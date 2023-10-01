package com.jvbarbosa.jcatalog.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NewPasswordDTO {

    private String token;
    @NotBlank(message = "Required field")
    @Size(min = 8, message = "Must have at least 8 characters")
    private String password;

    @JsonCreator
    public NewPasswordDTO(String token, String password) {
        this.token = token;
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }
}
