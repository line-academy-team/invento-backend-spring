package com.lineacademy.inventobackendspring.repositories;

import com.lineacademy.inventobackendspring.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}