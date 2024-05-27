package com.example1._DrumFx.drumFx.service;




import com.example1._DrumFx.drumFx.dto.UserDto;
import com.example1._DrumFx.drumFx.model.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findByEmail(String email);

    List<UserDto> findAllUsers();
}
