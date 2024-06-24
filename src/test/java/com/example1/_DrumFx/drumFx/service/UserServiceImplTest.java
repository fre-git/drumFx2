package com.example1._DrumFx.drumFx.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.model.Role;
import com.example1._DrumFx.drumFx.model.User;
import com.example1._DrumFx.drumFx.repository.RoleRepository;
import com.example1._DrumFx.drumFx.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDto userDto;
    private User user;

    @BeforeEach
    void init() {
        userDto = new UserDto();
        userDto.setUsername("testUser");
        userDto.setEmail("test@mail.com");
        userDto.setPassword("password");

        user = new User();
        user.setUsername("testUser");
        user.setEmail("test@mail.com");
    }

    @Test
    @DisplayName("Should save User")
    void saveUser() {

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.saveUser(userDto);

        assertNotNull(savedUser);
        assertThat(savedUser.getUsername()).isEqualTo("testUser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should return UserDto by email")
    void findByEmail() {
        String email = "test@mail.com";
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(user);

        UserDto foundUser = userService.findByEmail(email);

        assertNotNull(foundUser);
        assertThat(foundUser.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should return UserDto by username")
    void findByUsername() {
        String username = "testUser";
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDto foundUser = userService.findByUsername(username);

        assertNotNull(foundUser);
        assertThat(foundUser.getUsername()).isEqualTo(username);
    }
}
