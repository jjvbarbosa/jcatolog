package com.jvbarbosa.jcatalog.dto;

public class UserInsertDTO extends UserDTO {

    private String password;

    public UserInsertDTO(Long id, String firstName, String lastName, String email, String password) {
        super(id, firstName, lastName, email);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}