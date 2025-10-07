package com.fixitnow.repository;

import com.fixitnow.model.Role;
import com.fixitnow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
    long countByRole(Role role);
    List<User> findByRole(Role role);
}
