package com.example1._DrumFx.drumFx.service;

import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.model.User;

public interface UserService {
    User saveUser(UserDto userDto);

    UserDto findByEmail(String email);

    UserDto findByUsername(String username);

    void updateUser(UserDto userDto);
}
