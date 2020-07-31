package org.example.service.impl;

import org.example.config.OrikaConfig;
import org.example.dto.FilterRequestDto;
import org.example.dto.LoginDto;
import org.example.dto.UserProfileDto;
import org.example.entity.Employer;
import org.example.entity.Freelancer;
import org.example.entity.Skill;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;
import org.example.repository.AuthorityRepository;
import org.example.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @Mock
    private AuthorityRepository authorityRepository;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void save_negativeTest() {
        LoginDto loginDto = new LoginDto("freelancer", "qwe", false);
        when(userRepository.findByLogin("freelancer")).thenReturn(Optional.of(Freelancer.builder().id(1L).build()));
        assertFalse(userService.save(loginDto));
    }

    @Test
    public void save_positiveTest() {
        LoginDto loginDto = new LoginDto("freelancer", "qwe", false);
        when(userRepository.findByLogin("freelancer")).thenReturn(Optional.empty());
        assertTrue(userService.save(loginDto));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void getDtoById_ifUserWithIdIsNotPresent() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = assertThrows(FailedRequestError.class, () ->
                userService.getDtoById(1L));
        assertEquals("is not user with same id", exception.getMessage());
    }

    @Test
    public void getDtoById_positiveTest() throws FailedRequestError {
        Freelancer freelancer = Freelancer.builder().id(1L).login("freelancer")
                .imgSrc("src/main/resources/img/common/standard_user_avatar.jpg").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(freelancer));

        UserProfileDto expectedDto = OrikaConfig.getMapperFacade()
                .map(freelancer, UserProfileDto.class);
        assertEquals(expectedDto, userService.getDtoById(1L));
    }

    @Test
    public void getFreelancerDtoByFilter_ifFilterIsNull() {
        List<User> testList = Arrays.asList(
                Employer.builder().id(4L).login("employer").build(),
                Freelancer.builder().id(1L).login("freelancer1").imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .rating(1.5).skills(Collections.singletonList(new Skill("skill"))).build(),
                Freelancer.builder().id(2L).login("freelancer2").imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .rating(4.5).build(),
                Freelancer.builder().id(3L).login("freelancer3").imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .build()
        );
        when(userRepository.findAll()).thenReturn(testList);

        List<UserProfileDto> expectedList = OrikaConfig.getMapperFacade().mapAsList(testList.subList(1, 4), UserProfileDto.class);

        assertEquals(expectedList, userService.getFreelancerDtoByFilter(new FilterRequestDto(null, null), PageRequest.of(0, 5)));
    }

    @Test
    public void getFreelancerDtoByFilter_ifFilterBySkills() {
        List<Freelancer> testList = Arrays.asList(
                Freelancer.builder().id(1L).login("freelancer1").imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .rating(1.5).skills(Collections.singletonList(new Skill("skill"))).build(),
                Freelancer.builder().id(2L).login("freelancer2").imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .rating(4.5).skills(Collections.singletonList(new Skill("skill"))).build()
        );

        FilterRequestDto filterRequestDto = new FilterRequestDto(Collections.singletonList(new Skill("skill")), null);
        when(userRepository.findBySkillsIn(filterRequestDto.getSkills(), PageRequest.of(0, 5)))
                .thenReturn(testList);

        List<UserProfileDto> expectedList = OrikaConfig.getMapperFacade()
                .mapAsList(testList, UserProfileDto.class);

        assertEquals(expectedList, userService.getFreelancerDtoByFilter(filterRequestDto, PageRequest.of(0, 5)));
    }

    @Test
    public void getFreelancerDtoByFilter_ifFilterByRating() {
        List<Freelancer> testList = Arrays.asList(
                Freelancer.builder().id(1L).login("freelancer1").imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .rating(4.1).build(),
                Freelancer.builder().id(2L).login("freelancer2").imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .rating(4.5).build()
        );

        FilterRequestDto filterRequestDto = new FilterRequestDto(null, 4.0);
        when(userRepository.findByRating(filterRequestDto.getRating(), PageRequest.of(0, 5)))
                .thenReturn(testList);

        List<UserProfileDto> expectedList = OrikaConfig.getMapperFacade()
                .mapAsList(testList, UserProfileDto.class);

        assertEquals(expectedList, userService.getFreelancerDtoByFilter(filterRequestDto, PageRequest.of(0, 5)));
    }

    @Test
    public void getFreelancerDtoByFilter_ifFilterBySkillsAndRating() {
        List<Freelancer> testList = Arrays.asList(
                Freelancer.builder().id(1L).login("freelancer1").rating(4.1).imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .skills(Collections.singletonList(new Skill("skill"))).build(),
                Freelancer.builder().id(2L).login("freelancer2").rating(4.5).imgSrc("src/main/resources/img/common/standard_user_avatar.jpg")
                        .skills(Collections.singletonList(new Skill("skill"))).build()
        );

        FilterRequestDto filterRequestDto = new FilterRequestDto(Collections.singletonList(new Skill("skill")), 4.0);
        when(userRepository.findByRatingAndSkillsIn(filterRequestDto.getRating(), filterRequestDto.getSkills(), PageRequest.of(0, 5)))
                .thenReturn(testList);
        List<UserProfileDto> expectedList = OrikaConfig.getMapperFacade()
                .mapAsList(testList, UserProfileDto.class);

        assertEquals(expectedList, userService.getFreelancerDtoByFilter(filterRequestDto, PageRequest.of(0, 5)));
    }

}