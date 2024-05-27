package com.example1._DrumFx.drumFx.repository;


import com.example1._DrumFx.drumFx.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
