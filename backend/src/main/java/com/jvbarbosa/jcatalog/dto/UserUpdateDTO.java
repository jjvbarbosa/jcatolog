package com.jvbarbosa.jcatalog.dto;

import com.jvbarbosa.jcatalog.services.validations.UserUpdateValid;

@UserUpdateValid
public class UserUpdateDTO extends UserDTO {

    public UserUpdateDTO(Long id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email);
    }
}
