package com.jvbarbosa.jcatalog.services;

import com.jvbarbosa.jcatalog.dto.UserDTO;
import com.jvbarbosa.jcatalog.dto.UserInsertDTO;
import com.jvbarbosa.jcatalog.dto.UserUpdateDTO;
import com.jvbarbosa.jcatalog.entities.Role;
import com.jvbarbosa.jcatalog.entities.User;
import com.jvbarbosa.jcatalog.projections.UserDetailsProjection;
import com.jvbarbosa.jcatalog.repositories.RoleRepository;
import com.jvbarbosa.jcatalog.repositories.UserRepository;
import com.jvbarbosa.jcatalog.services.exceptions.DatabaseException;
import com.jvbarbosa.jcatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public UserDTO findMyProfile() {
        User entity = authenticated();
        return new UserDTO(entity);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Resource with ID '" + id + "' not found"));
        return new UserDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(x -> new UserDTO(x));
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();

        copyDtoToEntity(dto, entity);

        entity.getRoles().clear();
        Role role = roleRepository.findByAuthority("ROLE_OPERATOR");
        entity.getRoles().add(role);

        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        entity = repository.save(entity);

        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        try {
            User entity = repository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = repository.save(entity);

            return new UserDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Resource with ID '" + id + "' not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Resource with ID '" + id + "' not found");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential Integrity Violation");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }

        User user = new User();
        user.setEmail(result.get(0).getUsername());
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }

    protected User authenticated() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");
            return repository.findByEmail(username);
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Invalid user");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity) {
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());

        entity.getRoles().clear();
        dto.getRoles().forEach(roleDTO -> {
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            entity.getRoles().add(role);
        });
    }
}
