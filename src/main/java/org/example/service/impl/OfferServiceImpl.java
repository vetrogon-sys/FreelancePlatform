package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.config.OrikaConfig;
import org.example.dto.OfferDto;
import org.example.entity.*;
import org.example.exceptions.FailedRequestError;
import org.example.repository.JobRepository;
import org.example.repository.OfferRepository;
import org.example.service.OfferService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {
    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;
    private final UserService userService;

    @Override
    public void confirm(Long jobId, Long offerId) throws FailedRequestError {
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job != null) {
            Offer offer = offerRepository.findById(offerId).orElse(null);
            if (offer != null) {
                job.setStage(Stage.IN_DEVELOPING);
                job.setFreelancer(offer.getFreelancer());

                List<Offer> offers = StreamSupport.stream(offerRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());

                for (Offer off : offers) {
                    if (off.getJob().equals(job)) {
                        if (off.equals(offer)) {
                            off.setState(State.CONFIRMED);
                        } else {
                            off.setState(State.REJECTED);
                        }
                    }
                }
                offerRepository.saveAll(offers);
                jobRepository.save(job);
            } else {
                throw new FailedRequestError("is not offer with same id");
            }
        } else {
            throw new FailedRequestError("is not job with same id");
        }
    }

    @Override
    public OfferDto getDtoById(Long jobId, Long offerId) throws FailedRequestError {
        if (jobRepository.existsById(jobId)) {
            Offer offer = offerRepository.findById(offerId).orElse(null);
            if (offer != null) {
                return OrikaConfig
                        .getMapperFacade()
                        .map(offer, OfferDto.class);
            } else {
                throw new FailedRequestError("there is not offer with same id");
            }
        } else {
            throw new FailedRequestError("there is not job with same id");
        }
    }

    @Override
    public List<OfferDto> getDtoList(Long jobId) throws FailedRequestError {
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job != null) {
            List<Offer> offers = offerRepository.findAllByJob(job);

            return OrikaConfig
                    .getMapperFacade()
                    .mapAsList(offers, OfferDto.class);
        } else {
            throw new FailedRequestError("is not job with same id");
        }
    }

    @Override
    public void create(Long jobId) throws FailedRequestError {
        User user = userService.getUserFromSecurityContext();
        if (user.getClass().equals(Freelancer.class)) {
            Job job = jobRepository.findById(jobId).orElse(null);
            if (job != null) {
                if (job.getStage().equals(Stage.POSTED)) {
                    offerRepository.save(Offer.builder()
                            .job(job)
                            .freelancer((Freelancer) user)
                            .state(State.EXPECTATION)
                            .build());
                } else {
                    throw new FailedRequestError("the job is in the development garden");
                }
            } else {
                throw new FailedRequestError("there is not job with same id");
            }
        } else {
            throw new FailedRequestError("only freelancer can send offers");
        }
    }
}
