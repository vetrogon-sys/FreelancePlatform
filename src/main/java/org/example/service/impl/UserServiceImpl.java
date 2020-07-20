package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.config.OrikaConfig;
import org.example.dto.FilterRequestDto;
import org.example.dto.LoginDto;
import org.example.dto.UserParamsDto;
import org.example.dto.UserProfileDto;
import org.example.entity.Authority;
import org.example.entity.Employer;
import org.example.entity.Freelancer;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;
import org.example.repository.AuthorityRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
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

    @Override
    public Optional<User> findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
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
        user.setImgSrc("src" +
                "\\main" +
                "\\resources" +
                "\\img" +
                "\\common" +
                "\\standard_user_avatar.jpg");
        user.setCreatedOn(LocalDateTime.now().withNano(0));
        userRepository.save(user);
        return true;
    }

    @Override
    public void update(UserParamsDto userParamsDto) throws FailedRequestError {
        User currentUser = getUserFromSecurityContext();
        if (currentUser != null) {
            currentUser.setName(userParamsDto.getName());
            currentUser.setSurname(userParamsDto.getSurname());

            if (!userParamsDto.getFileSrc().isEmpty()) {
                saveImg(userParamsDto.getFileSrc(), currentUser);
            }

            if (currentUser.getClass().equals(Freelancer.class)) {
                ((Freelancer) currentUser).setSkills(userParamsDto.getSkills());
            }
            userRepository.save(currentUser);
        } else {
            throw new FailedRequestError("is not logged users");
        }
    }

    private void saveImg(String fileSrc, User user) throws FailedRequestError {
        try {
            Path path = Paths.get(fileSrc);
            byte[] fileBytes = Files.readAllBytes(path);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));

            String imgSrc = "src" +
                    "\\main" +
                    "\\resources" +
                    "\\img" +
                    "\\users_" + user.getId()
                    + "_avatarImg.jpg";
            File destination = new File(imgSrc);
            ImageIO.write(image, "jpg", destination);

            user.setImgSrc(imgSrc);

        } catch (IOException e) {
            throw new FailedRequestError("Fie load is failed");
        }
    }

    @Override
    public void update(Object object) {
        if (object.getClass().equals(Freelancer.class)) {
            userRepository.save((Freelancer) object);
        } else if (object.getClass().equals(Employer.class)) {
            userRepository.save((Employer) object);
        }
    }

    @Override
    public UserProfileDto getDtoById(Long id) throws FailedRequestError {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return OrikaConfig
                    .getMapperFacade()
                    .map(user, UserProfileDto.class);
        } else {
            throw new FailedRequestError("is not user with same id");
        }
    }

    @Override
    public UserProfileDto getCurrentDto() throws FailedRequestError {
        User currentUser = getUserFromSecurityContext();
        if (currentUser != null) {
            return OrikaConfig
                    .getMapperFacade()
                    .map(currentUser, UserProfileDto.class);
        } else {
            throw new FailedRequestError("is not logged users");
        }
    }

    @Override
    public List<UserProfileDto> getFreelancerDtoByFilter(FilterRequestDto filterRequestDto, Pageable pageable) {
        List<Freelancer> freelancerList;

        if (filterRequestDto.getSkills() != null
                && filterRequestDto.getRating() != null) {
            List<Freelancer> skillList = userRepository.findBySkillsIn(filterRequestDto.getSkills(), pageable);
            List<Freelancer> ratingList = userRepository.findByRating(filterRequestDto.getRating(), pageable);
            freelancerList = skillList.stream()
                    .filter(ratingList::contains)
                    .collect(Collectors.toList());

        } else if (filterRequestDto.getSkills() != null) {
            freelancerList = userRepository.findBySkillsIn(filterRequestDto.getSkills(), pageable);
        } else if (filterRequestDto.getRating() != null) {
            freelancerList = userRepository.findByRating(filterRequestDto.getRating(), pageable);
        } else {
            freelancerList = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList())
                    .stream()
                    .filter(e -> e.getClass().equals(Freelancer.class))
                    .map(e -> (Freelancer) e)
                    .collect(Collectors.toList());
        }

//        return OrikaConfig
//                .getMapperFacade()
//                .mapAsList(freelancerList, UserProfileDto.class);

        return freelancerList.stream()
                .map(e -> OrikaConfig.getMapperFacade().map(e, UserProfileDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserFromSecurityContext() {
        return userRepository.findByLogin(((org.springframework.security.core.userdetails.User)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()).orElseThrow(() -> new IllegalArgumentException("meeasge"));
    }
}
