package org.example.service.impl;

import org.example.config.OrikaConfig;
import org.example.dto.ReviewDto;
import org.example.entity.Employer;
import org.example.entity.Freelancer;
import org.example.entity.Review;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;
import org.example.repository.ReviewRepository;
import org.example.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    public void create_WhenRequestFailed() {
        when(userService.getUserFromSecurityContext()).thenReturn(Freelancer.builder().id(1L).login("freelancer").build());

        Exception exception = assertThrows(FailedRequestError.class, () ->
                reviewService.create(new ReviewDto(), 2L));
        assertEquals("Invalid request", exception.getMessage());
    }

    @Test
    public void create_WhenFreelancerIsNull() {
        when(userService.getUserFromSecurityContext()).thenReturn(Employer.builder().id(1L).login("employer").build());

        Exception exception = assertThrows(FailedRequestError.class, () ->
                reviewService.create(new ReviewDto(), 2L));
        assertEquals("incorrect data", exception.getMessage());
    }

    @Test
    public void create_Test() throws FailedRequestError {
        when(userService.getUserFromSecurityContext()).thenReturn(Employer.builder().id(1L).login("employer").build());
        when(userService.findById(2L)).thenReturn(Optional.of(Freelancer.builder().id(2L).login("freelancer").build()));

        reviewService.create(new ReviewDto(), 2L);
        verify(reviewRepository, times(1)).save(any(Review.class));
        verify(userService, times(1)).update(any(User.class));
    }

    @Test
    public void getReviewListById_WhenUserIsNotPresent() {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FailedRequestError.class, () ->
                reviewService.getReviewListById(1L));
        assertEquals("Is not user with same id", exception.getMessage());
    }

    @Test
    public void getReviewListById_WhenUserIsPresent() throws FailedRequestError {
        List<Review> expectedReviews = Arrays.asList(
                Review.builder().message("review_message_1").build(),
                Review.builder().message("review_message_2").build()
        );
        when(userService.findById(1L)).thenReturn(Optional.ofNullable(Employer.builder().id(1L).reviews(expectedReviews).build()));
        List<ReviewDto> expectedReviewDto = OrikaConfig.getMapperFacade()
                .mapAsList(expectedReviews, ReviewDto.class);

        assertEquals(expectedReviewDto, reviewService.getReviewListById(1L));
    }
}