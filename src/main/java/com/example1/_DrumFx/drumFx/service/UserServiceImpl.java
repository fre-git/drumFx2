package com.example1._DrumFx.drumFx.service;


import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.model.Role;
import com.example1._DrumFx.drumFx.model.User;
import com.example1._DrumFx.drumFx.repository.RoleRepository;
import com.example1._DrumFx.drumFx.repository.UserRepository;
import com.example1._DrumFx.drumFx.util.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//@Component
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public User saveUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role role;
        if (userRepository.findAll().isEmpty()) {
            initiateRoles();
        }
        role = roleRepository.findByName("ROLE_USER");

        user.getRoles().add(role);
        userRepository.save(user);
        return user;
    }

    @Override
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return null;
        }
        return DtoMapper.mapToUserDto(user);
    }

    @Override
    public UserDto findByUsername(String username) {
        return DtoMapper.mapToUserDto(userRepository.findByUsername(username));
    }

    @Override
    public void updateUser(UserDto userDto) {
        User userToUpdate = userRepository.getReferenceById(userDto.getId());
        userToUpdate.setProfilePicture(userDto.getProfilePicture());
        userRepository.save(userToUpdate);
    }

    private void initiateRoles() {
        Role admin = new Role();
        admin.setName("ROLE_ADMIN");
        Role user = new Role();
        user.setName("ROLE_USER");
        roleRepository.save(admin);
        roleRepository.save(user);
    }
}
