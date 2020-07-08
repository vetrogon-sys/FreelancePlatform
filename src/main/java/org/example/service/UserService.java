package org.example.service;

import org.example.dto.LoginDto;
import org.example.dto.UserParamsDto;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;

import java.util.Optional;

public interface UserService {
    Optional<User> findByLogin(String login);
    boolean save(LoginDto loginDto);
    void update(UserParamsDto userParamsDto, Long userId) throws FailedRequestError;
    User getUserFromSecurityContext();
}
