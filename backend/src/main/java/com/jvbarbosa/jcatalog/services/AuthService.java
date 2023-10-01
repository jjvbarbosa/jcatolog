package com.jvbarbosa.jcatalog.services;

import com.jvbarbosa.jcatalog.dto.EmailDTO;
import com.jvbarbosa.jcatalog.dto.NewPasswordDTO;
import com.jvbarbosa.jcatalog.entities.PasswordRecover;
import com.jvbarbosa.jcatalog.entities.User;
import com.jvbarbosa.jcatalog.repositories.PasswordRecoverRepository;
import com.jvbarbosa.jcatalog.repositories.UserRepository;
import com.jvbarbosa.jcatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createRecoverToken(EmailDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("Email not found");
        }

        String token = UUID.randomUUID().toString();
        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(dto.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));

        entity = passwordRecoverRepository.save(entity);

        String body = "Access the link to set a new password\n\n"
                + recoverUri + token + "\n\nLink valid for " + tokenMinutes + " minutes.";
        emailService.sendEmail(dto.getEmail(), "Password recovery", body);
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO dto) {
        List<PasswordRecover> result = passwordRecoverRepository.searchValidTokens(dto.getToken(), Instant.now());
        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Invalid token");
        }

        User user = userRepository.findByEmail(result.get(0).getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);
    }
}
