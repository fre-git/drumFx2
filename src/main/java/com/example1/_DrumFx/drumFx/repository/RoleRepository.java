package com.example1._DrumFx.drumFx.repository;


import com.example1._DrumFx.drumFx.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
