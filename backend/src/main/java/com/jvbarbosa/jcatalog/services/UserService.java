package com.jvbarbosa.jcatalog.services;

import com.jvbarbosa.jcatalog.dto.UserDTO;
import com.jvbarbosa.jcatalog.dto.UserInsertDTO;
import com.jvbarbosa.jcatalog.dto.UserUpdateDTO;
import com.jvbarbosa.jcatalog.entities.Role;
import com.jvbarbosa.jcatalog.entities.User;
import com.jvbarbosa.jcatalog.repositories.RoleRepository;
import com.jvbarbosa.jcatalog.repositories.UserRepository;
import com.jvbarbosa.jcatalog.services.exceptions.DatabaseException;
import com.jvbarbosa.jcatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

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
