package com.itclass.controller;

import com.itclass.dto.RestResponse;
import com.itclass.entity.Job;
import com.itclass.entity.Offer;
import com.itclass.entity.User;
import com.itclass.repository.JobRepository;
import com.itclass.repository.OfferRepository;
import com.itclass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final UserService userService;
    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;

    @GetMapping
    public RestResponse getJobs() {
        return RestResponse.builder()
                .isSuccess(true)
                .response(StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList()))
                .build();
    }

    @GetMapping("/{id}")
    public RestResponse findJobByIid(@PathVariable Long id) {
        return RestResponse.builder()
                .isSuccess(true)
                .response(jobRepository.findById(id).orElse(null))
                .build();
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job,
                                       @AuthenticationPrincipal User user) {
        Job savedJob = jobRepository.save(job);
        return ResponseEntity.created(URI.create("/jobs/"+savedJob.getId())).body("Created");
    }

    @PostMapping("/{id}/offer")
    public ResponseEntity<?> createOffer(@PathVariable Long id) {
        User user = userService.getUserFromSecurityContext();
        if (jobRepository.existsById(id)) {
            Offer savedOffer = offerRepository.save(Offer.builder()
                    .job(jobRepository.findById(id).orElse(null))
                    .build());
            return ResponseEntity.created(URI.create("/jobs/"+id+"/offer/"+savedOffer.getId())).body("Created");
        } else {
            return ResponseEntity.created(URI.create("/jobs/")).body("There isn't job with same id");
        }
    }
}
