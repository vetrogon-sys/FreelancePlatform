package org.example.service.impl;

import org.example.config.OrikaConfig;
import org.example.dto.JobDto;
import org.example.dto.OfferDto;
import org.example.entity.*;
import org.example.exceptions.FailedRequestError;
import org.example.repository.JobRepository;
import org.example.repository.OfferRepository;
import org.example.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private OfferServiceImpl offerService;

    @Test
    public void confirm_ifJobIsNotPresent() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.confirm(1L, 1L));
        assertEquals("there is not job with same id", exception.getMessage());
    }

    @Test
    public void confirm_ifOfferIsNotPresent() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(Job.builder().id(1L).name("job_1").stage(Stage.POSTED).build()));
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.confirm(1L, 1L));
        assertEquals("there is not offer with same id", exception.getMessage());
    }

    @Test
    public void confirm_positiveTest() throws FailedRequestError {
        Job job = Job.builder().id(1L).name("job_1").stage(Stage.POSTED).build();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(offerRepository.findById(1L)).thenReturn(Optional.of(Offer.builder().id(1L).job(job).build()));
        when(offerRepository.findAllByJob(job)).thenReturn(Arrays.asList(
                Offer.builder().id(1L).job(job).state(State.EXPECTATION).build(),
                Offer.builder().id(2L).job(job).state(State.EXPECTATION).build(),
                Offer.builder().id(3L).job(job).state(State.EXPECTATION).build()
        ));

        offerService.confirm(1L, 1L);
        verify(offerRepository,times(1)).saveAll(anyList());
        verify(jobRepository,times(1)).save(any(Job.class));
    }

    @Test
    public void getDtoById_ifJobIsNotPresent() {
        when(jobRepository.existsById(1L)).thenReturn(false);

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.getDtoById(1L, 1L));
        assertEquals("there is not job with same id", exception.getMessage());
    }

    @Test
    public void getDtoById_ifOfferIsNotPresent() {
        when(jobRepository.existsById(1L)).thenReturn(true);
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.getDtoById(1L, 1L));
        assertEquals("there is not offer with same id", exception.getMessage());
    }

    @Test
    public void getDtoById_positiveTest() throws FailedRequestError {
        when(jobRepository.existsById(1L)).thenReturn(true);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(Offer.builder()
                .id(1L)
                .job(Job.builder().id(1L).name("job_1").stage(Stage.POSTED).build())
                .state(State.EXPECTATION)
                .build()));

        OfferDto expectedDto = OfferDto.builder()
                .job(JobDto.builder()
                        .name("job_1")
                        .stage(Stage.POSTED)
                        .build())
                .build();
        assertEquals(expectedDto, offerService.getDtoById(1L, 1L));
    }

    @Test
    public void getDtoList_ifJobIsNotPresent() {
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.getDtoList(1L));
        assertEquals("there is not job with same id", exception.getMessage());
    }

    @Test
    public void getDtoList_positiveTest() throws FailedRequestError {
        Job job = Job.builder().id(1L).name("job_1").stage(Stage.POSTED).build();
        when(jobRepository.findById(1L)).thenReturn(Optional.of(job));
        when(offerRepository.findAllByJob(job)).thenReturn(Arrays.asList(
                Offer.builder().id(1L).job(job).state(State.EXPECTATION).build(),
                Offer.builder().id(2L).job(job).state(State.EXPECTATION).build(),
                Offer.builder().id(3L).job(job).state(State.EXPECTATION).build()
        ));

        List<OfferDto> expectedList = OrikaConfig.getMapperFacade()
                .mapAsList(Arrays.asList(
                        Offer.builder().id(1L).job(job).state(State.EXPECTATION).build(),
                        Offer.builder().id(2L).job(job).state(State.EXPECTATION).build(),
                        Offer.builder().id(3L).job(job).state(State.EXPECTATION).build()
                ), OfferDto.class);
        assertEquals(expectedList, offerService.getDtoList(1L));
    }

    @Test
    public void create_ifUserIsNotPresent() {
        when(userService.getUserFromSecurityContext()).thenReturn(null);

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.create(1L));
        assertEquals("only freelancer can send offers", exception.getMessage());
    }

    @Test
    public void create_ifJobIsNotPresent() {
        when(userService.getUserFromSecurityContext()).thenReturn(Freelancer.builder().id(1L).build());
        when(jobRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.create(1L));
        assertEquals("there is not job with same id", exception.getMessage());
    }

    @Test
    public void create_ifJobIsBeingDevelopment() {
        when(userService.getUserFromSecurityContext()).thenReturn(Freelancer.builder().id(1L).build());
        when(jobRepository.findById(1L)).thenReturn(Optional.of(Job.builder().id(1L).name("job_1").stage(Stage.IN_DEVELOPING).build()));

        Exception exception = Assert.assertThrows(FailedRequestError.class, () ->
                offerService.create(1L));
        assertEquals("the job is in the development garden", exception.getMessage());
    }

    @Test
    public void create_positiveTest() throws FailedRequestError {
        when(userService.getUserFromSecurityContext()).thenReturn(Freelancer.builder().id(1L).build());
        when(jobRepository.findById(1L)).thenReturn(Optional.of(Job.builder().id(1L).name("job_1").stage(Stage.POSTED).build()));

        offerService.create(1L);
        verify(offerRepository, times(1)).save(any(Offer.class));
    }
}