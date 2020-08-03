package org.example.service.impl;

import org.example.config.OrikaConfig;
import org.example.dto.FilterRequestDto;
import org.example.dto.JobDto;
import org.example.entity.*;
import org.example.exceptions.FailedRequestError;
import org.example.repository.JobRepository;
import org.example.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JobServiceTest {
    private static final User employer = Employer.builder()
            .id(1L)
            .login("employer")
            .password("password")
            .build();
    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private JobServiceImpl jobService;

    @Before
    public void setUp() throws Exception {
        Job expectedJob = Job.builder().id(1L).name("job_1").employer((Employer) employer).stage(Stage.POSTED).build();

        when(jobRepository.findById(1L)).thenReturn(
                Optional.of(expectedJob)
        );

        when(jobRepository.findById(2L)).thenReturn(
                Optional.empty()
        );
    }

    @Test
    public void getDtoListByFilter_WithNullFilter() {
        FilterRequestDto filterRequestDto = new FilterRequestDto(null, null);
        Pageable pageable = PageRequest.of(0, 5);

        when(jobRepository.findAllByStage(Stage.POSTED, pageable)).thenReturn(Arrays.asList(
           Job.builder().id(1L).name("job_1").employer((Employer) employer).stage(Stage.POSTED).build(),
           Job.builder().id(2L).name("job_2").employer((Employer) employer).stage(Stage.POSTED).build(),
           Job.builder().id(3L).name("job_3").employer((Employer) employer).stage(Stage.POSTED).build(),
           Job.builder().id(4L).name("job_4").employer((Employer) employer).stage(Stage.POSTED).build(),
           Job.builder().id(5L).name("job_5").employer((Employer) employer).stage(Stage.POSTED).build()
        ));

        assertEquals(5, jobService.getDtoListByFilter(filterRequestDto, pageable).size());
    }

    @Test
    public void getDtoListByFilter_WithSkillFilter() {
        FilterRequestDto filterRequestDto = new FilterRequestDto(Arrays.asList(
                new Skill("skill_1")
        ), null);
        Pageable pageable = PageRequest.of(0, 5);

        when(jobRepository.findBySkillsInAndStage(filterRequestDto.getSkills(), Stage.POSTED, pageable)).thenReturn(Arrays.asList(
                Job.builder().id(2L).name("job_2").employer((Employer) employer).stage(Stage.POSTED).skills(Arrays.asList(new Skill("skill_1"))).build(),
                Job.builder().id(3L).name("job_3").employer((Employer) employer).stage(Stage.POSTED).skills(Arrays.asList(new Skill("skill_1"))).build()
        ));

        assertEquals(2, jobService.getDtoListByFilter(filterRequestDto, pageable).size());
    }

    @Test
    public void getDtoById_WhenJobIsPresent() throws FailedRequestError {
        Job expectedJob = Job.builder().id(1L).name("job_1").employer((Employer) employer).stage(Stage.POSTED).build();
        JobDto expectedJobDto = OrikaConfig.getMapperFacade()
                .map(expectedJob, JobDto.class);
        assertNotNull(jobService.getDtoById(1L));
        assertEquals(expectedJobDto, jobService.getDtoById(1L));
    }

    @Test
    public void getDtoById_WhenJobIsNotPresent() {
        Exception exception = assertThrows(FailedRequestError.class, () ->
                jobService.getDtoById(2L));
        assertEquals("There is not job with same id", exception.getMessage());
    }

    @Test
    public void create_WhenUserIsNotEmployer() {
        when(userService.getUserFromSecurityContext()).thenReturn(null);

        Exception exception = assertThrows(FailedRequestError.class, () ->
                jobService.create(new JobDto()));
        assertEquals("only employer can create job", exception.getMessage());
    }

    @Test
    public void create_WhenUserIsEmployer() throws FailedRequestError {
        when(userService.getUserFromSecurityContext()).thenReturn(employer);

        JobDto jobDto = JobDto.builder().name("jobDto_name").build();
        jobService.create(jobDto);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    public void close_WhenJobIsNotPresent() {
        Exception exception = assertThrows(FailedRequestError.class, () ->
                jobService.close(2L, false));
        assertEquals("There is not job with same id", exception.getMessage());
    }

    @Test
    public void close_WhenJobIsPresent_And_PerformedTrue() throws FailedRequestError {
        jobService.close(1L, true);
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    public void close_WhenJobIsPresent_And_PerformedFalse() throws FailedRequestError {
        jobService.close(1L, false);
        verify(jobRepository, times(1)).save(any(Job.class));
    }
}