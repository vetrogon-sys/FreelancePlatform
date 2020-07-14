package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.RestResponse;
import org.example.dto.ReviewDto;
import org.example.exceptions.FailedRequestError;
import org.example.service.ReviewService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/reviews")
    @PreAuthorize("hasRole('EMPLOYER')")
    //ID of user to send review
    public RestResponse createReview(@PathVariable Long userId,
                                     @RequestBody ReviewDto reviewDto) {
        try {
            reviewService.create(reviewDto, userId);
            return RestResponse.generateSuccessfulResponse("Created");
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @GetMapping("/reviews")
    @PreAuthorize("authenticated()")
    public RestResponse getReviews(@PathVariable Long userId) {
        try {
            return RestResponse.generateSuccessfulResponse(reviewService.getReviewListById(userId));
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }
}
