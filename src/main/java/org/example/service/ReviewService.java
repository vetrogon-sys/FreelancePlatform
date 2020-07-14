package org.example.service;

import org.example.dto.ReviewDto;
import org.example.exceptions.FailedRequestError;

import java.util.List;

public interface ReviewService {
    void create(ReviewDto reviewDto, Long freelancerId) throws FailedRequestError;
    List<ReviewDto> getReviewListById(Long id) throws FailedRequestError;

}
