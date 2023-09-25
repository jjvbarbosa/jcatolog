package com.jvbarbosa.jcatalog.repositories;

import com.jvbarbosa.jcatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
