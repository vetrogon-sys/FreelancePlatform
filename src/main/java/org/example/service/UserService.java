package org.example.service;

import org.example.dto.LoginDto;
import org.example.dto.UserParamsDto;
import org.example.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByLogin(String login);
    boolean save(LoginDto loginDto);
    boolean update(User user, UserParamsDto userParamsDto);
    User getUserFromSecurityContext();
}
