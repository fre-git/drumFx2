package com.example1._DrumFx.drumFx.repository;

import com.example1._DrumFx.drumFx.model.User;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


@DataJpaTest
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @Test
    public void testFindByEmail() {
        String email = "test@gmail.com";
        User user = new User();
        user.setId(1L);
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        User foundUser = userRepository.findByEmail(email);

        assertEquals(email, foundUser.getEmail());
    }


    @Test
    public void testFindByUsername() {
        String username = "testUser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        User foundUser = userRepository.findByUsername(username);

        assertEquals(username, foundUser.getUsername());
    }
}