package org.example.controller;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.example.dto.JobDto;
import org.example.dto.OfferDto;
import org.example.dto.RestResponse;
import org.example.dto.UserDto;
import org.example.entity.*;
import org.example.repository.JobRepository;
import org.example.repository.OfferRepository;
import org.example.repository.StageRepository;
import org.example.repository.StateRepository;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/jobs/{jobId}/offers")
@RequiredArgsConstructor
public class OfferController {
    private final OfferRepository offerRepository;
    private final JobRepository jobRepository;
    private final UserService userService;
    private final StageRepository stageRepository;
    private final StateRepository stateRepository;
    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @PutMapping("/{id}")
    public RestResponse confirmOffer(@PathVariable Long id, @PathVariable Long jobId) {
        Job job = jobRepository.findById(jobId).orElse(null);
        if (job != null) {
            Offer offer = offerRepository.findById(id).orElse(null);
            if (offer != null) {
                job.setStage(stageRepository.findById("В разработке").orElse(null));
                job.setFreelancer(offer.getFreelancer());

                List<Offer> offers = StreamSupport.stream(offerRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
                State badState = stateRepository.findById("Отклонено").orElse(null);

                for (Offer off : offers) {
                    if (off.equals(offer)) {
                        off.setState(stateRepository.findById("Подтверждено").orElse(null));
                    } else {
                        off.setState(badState);
                    }
                }

                offerRepository.saveAll(offers);
                jobRepository.save(job);

                return RestResponse.builder()
                        .isSuccess(true)
                        .response("Confirmed")
                        .build();
            } else {
                return RestResponse.builder()
                        .isSuccess(false)
                        .response("is not offer with same id")
                        .build();
            }
        }
        return RestResponse.builder()
                .isSuccess(false)
                .response("is not job with same id")
                .build();
    }

    @GetMapping("/{id}")
    public RestResponse getOfferById(@PathVariable Long id, @PathVariable Long jobId) {
        if (jobRepository.existsById(jobId)) {
            Offer offer = offerRepository.findById(id).orElse(null);
            if (offer != null) {
                mapperFactory.classMap(User.class, UserDto.class);
                MapperFacade userMapper = mapperFactory.getMapperFacade();

                mapperFactory.classMap(Job.class, JobDto.class);
                MapperFacade jobMapper = mapperFactory.getMapperFacade();

                OfferDto offerDto = OfferDto.builder()
                        .freelancer(userMapper.map(offer.getFreelancer(), UserDto.class))
                        .job(jobMapper.map(offer.getJob(), JobDto.class))
                        .build();

                return RestResponse.builder()
                        .isSuccess(true)
                        .response(offerDto)
                        .build();
            }
            return RestResponse.builder()
                    .isSuccess(false)
                    .response("is not offer with same id")
                    .build();
        }
        return RestResponse.builder()
                .isSuccess(false)
                .response("is not job with same id")
                .build();
    }

    @PostMapping()
    public ResponseEntity<?> createOffer(@PathVariable Long jobId) {
        User user = userService.getUserFromSecurityContext();
        if (user.getClass().equals(Freelancer.class)) {
            if (jobRepository.existsById(jobId)) {
                Offer savedOffer = offerRepository.save(Offer.builder()
                        .job(jobRepository.findById(jobId).orElse(null))
                        .freelancer((Freelancer) user)
                        .state(stateRepository.findById("Ожидание").orElse(null))
                        .build());
                return ResponseEntity.created(URI.create("/jobs/" + jobId + "/offer/" + savedOffer.getId())).body("Created");
            } else {
                return ResponseEntity.badRequest().body("Is not job with that id");
            }
        }
        return ResponseEntity.badRequest().body("Only freelancer can send offers");
    }
}
