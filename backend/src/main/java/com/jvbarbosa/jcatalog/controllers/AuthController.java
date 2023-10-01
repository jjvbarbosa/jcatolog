package com.jvbarbosa.jcatalog.controllers;

import com.jvbarbosa.jcatalog.dto.EmailDTO;
import com.jvbarbosa.jcatalog.dto.NewPasswordDTO;
import com.jvbarbosa.jcatalog.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody EmailDTO dto) {
        service.createRecoverToken(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto) {
        service.saveNewPassword(dto);
        return ResponseEntity.noContent().build();
    }
}
