package org.example.service;

import org.example.dto.FilterRequestDto;
import org.example.dto.LoginDto;
import org.example.dto.UserProfileDto;
import org.example.dto.UserParamsDto;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByLogin(String login);
    Optional<User> findById(Long id);
    boolean save(LoginDto loginDto);
    void update(UserParamsDto userParamsDto) throws FailedRequestError;
    void update(Object object);
    UserProfileDto getDtoById(Long id) throws FailedRequestError;
    UserProfileDto getCurrentDto() throws FailedRequestError;
    List<UserProfileDto> getFreelancerDtoByFilter(FilterRequestDto filterRequestDto, Pageable pageable);
    User getUserFromSecurityContext();
}
