package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.LoginDto;
import org.example.dto.UserParamsDto;
import org.example.entity.Authority;
import org.example.entity.Employer;
import org.example.entity.Freelancer;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;
import org.example.repository.AuthorityRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthorityRepository authorityRepository;

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
    public User getUserFromSecurityContext() {
        return userRepository.findByLogin(((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).orElseThrow(() -> new IllegalArgumentException("meeasge"));
    }
}
