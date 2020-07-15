package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.config.OrikaConfig;
import org.example.dto.ReviewDto;
import org.example.entity.Employer;
import org.example.entity.Freelancer;
import org.example.entity.Review;
import org.example.exceptions.FailedRequestError;
import org.example.repository.ReviewRepository;
import org.example.service.ReviewService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Override
    public void create(ReviewDto reviewDto, Long freelancerId) throws FailedRequestError {
        Employer employer = (Employer) userService.getUserFromSecurityContext();
        Freelancer freelancer = (Freelancer) userService.findById(freelancerId).orElse(null);
        if (employer != null && freelancer != null) {
            Review review = OrikaConfig
                    .getMapperFacade()
                    .map(reviewDto, Review.class);
            review.setEmployer(employer);
            review.setFreelancer(freelancer);
            reviewRepository.save(review);

            Freelancer.updateRating(freelancer);
            userService.update(freelancer);
        } else {
            throw new FailedRequestError("incorrect data");
        }
    }

    @Override
    public List<ReviewDto> getReviewListById(Long userId) throws FailedRequestError {
        Employer employer = (Employer) userService.findById(userId).orElse(null);
        if (employer != null) {
            return OrikaConfig
                    .getMapperFacade()
                    .mapAsList(employer.getReviews(), ReviewDto.class);
        } else {
            throw new FailedRequestError("Is not user with same id");
        }
    }

}
