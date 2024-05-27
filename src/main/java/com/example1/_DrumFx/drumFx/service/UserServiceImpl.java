package com.example1._DrumFx.drumFx.service;



import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.model.Role;
import com.example1._DrumFx.drumFx.model.User;
import com.example1._DrumFx.drumFx.repository.RoleRepository;
import com.example1._DrumFx.drumFx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;


    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());

        //encrypt the password
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role;
        if(userRepository.findAll().isEmpty()){
            initiateRoles();
            role = roleRepository.findByName("ROLE_ADMIN");

        } else {
            role = roleRepository.findByName("ROLE_USER");
        }

        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    private UserDto convertEntityToDto(User user){
        UserDto userDto = new UserDto();
        String[] name = user.getName().split(" ");
        userDto.setFirstName(name[0]);
        userDto.setLastName(name[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
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
