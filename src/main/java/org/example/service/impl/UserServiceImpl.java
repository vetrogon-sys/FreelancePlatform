package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.config.OrikaConfig;
import org.example.dto.FilterRequestDto;
import org.example.dto.LoginDto;
import org.example.dto.UserDto;
import org.example.dto.UserParamsDto;
import org.example.entity.*;
import org.example.exceptions.FailedRequestError;
import org.example.repository.AuthorityRepository;
import org.example.repository.SkillRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthorityRepository authorityRepository;
    private final SkillRepository skillRepository;

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    @Transactional
    public boolean save(LoginDto loginDto) {
        if (findByLogin(loginDto.getUsername()).isPresent()) {
            return false;
        }
        String password = encoder.encode(loginDto.getPassword());
        Authority authority;

        User user;

        if (loginDto.isFreelancer()) {
            authority = authorityRepository.findById("ROLE_FREELANCER").orElse(null);
            user = Freelancer.builder()
                    .login(loginDto.getUsername())
                    .password(password)
                    .authorities(Collections.singletonList(authority))
                    .build();
        } else {
            authority = authorityRepository.findById("ROLE_EMPLOYER").orElse(null);
            user = Employer.builder()
                    .login(loginDto.getUsername())
                    .password(password)
                    .authorities(Collections.singletonList(authority))
                    .build();
        }
        user.setCreatedOn(LocalDateTime.now().withNano(0));
        userRepository.save(user);
        return true;
    }

    @Override
    public void update(UserParamsDto userParamsDto, Long userId) throws FailedRequestError {
        User currentUser = getUserFromSecurityContext();
        if (currentUser.getId().equals(userId)) {
            currentUser.setName(userParamsDto.getName());
            currentUser.setSurname(userParamsDto.getSurname());
            if (currentUser.getClass().equals(Freelancer.class)) {
                ((Freelancer) currentUser).setSkills(userParamsDto.getSkills());
            }
            userRepository.save(currentUser);
        } else {
            throw new FailedRequestError("Attempt to change another user");
        }
    }

    @Override
    public UserDto getDtoById(Long id) throws FailedRequestError {
        User currentUser = getUserFromSecurityContext();
        if (currentUser.getId().equals(id)) {
            return OrikaConfig.getMapperFactory()
                    .getMapperFacade()
                    .map(currentUser, UserDto.class);
        } else {
            throw new FailedRequestError("isn't logged users");
        }
    }

    @Override
    public List<UserDto> getFreelancerDtoList(FilterRequestDto filterRequestDto) {
        List<Freelancer> freelancerList = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(e -> e.getClass().equals(Freelancer.class))
                .map(e -> (Freelancer) e)
                .collect(Collectors.toList());

        List<Freelancer> filteredFreelancersList = new ArrayList<>();
        if (filterRequestDto.getFilterType().equals(FilterType.SKILL)) {
            Skill skill = skillRepository.findById(filterRequestDto.getValue()).orElse(null);
            filteredFreelancersList = freelancerList.stream()
                    .filter(e -> e.getSkills().contains(skill))
                    .collect(Collectors.toList());
        } else if (filterRequestDto.getFilterType().equals(FilterType.REGISTRATION_DATE)) {
            LocalDateTime localDateTime = LocalDateTime.parse(filterRequestDto.getValue());

            filteredFreelancersList = freelancerList.stream()
                    .filter(e -> e.getCreatedOn().isAfter(localDateTime))
                    .collect(Collectors.toList());
        }

        return filteredFreelancersList.stream()
                .map(e -> OrikaConfig.getMapperFactory()
                        .getMapperFacade()
                        .map(e, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserFromSecurityContext() {
        return userRepository.findByLogin(((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).orElseThrow(() -> new IllegalArgumentException("meeasge"));
    }
}
