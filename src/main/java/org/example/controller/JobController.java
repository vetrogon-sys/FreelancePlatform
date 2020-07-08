package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.FilterRequestDto;
import org.example.dto.JobDto;
import org.example.dto.RestResponse;
import org.example.entity.FilterType;
import org.example.exceptions.FailedRequestError;
import org.example.service.JobService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping
    public RestResponse getJobs(@RequestBody FilterRequestDto filterRequestDto) {
        if (filterRequestDto.getFilterType().equals(FilterType.SKILL)) {
            return RestResponse.generateSuccessfulResponse(jobService.getDtoListBySkills());
        } else {
            return RestResponse.generateSuccessfulResponse(jobService.getDtoList());
        }
    }

    @GetMapping("/{id}")
    public RestResponse findJobByIid(@PathVariable Long id) {
        try {
            return RestResponse.generateSuccessfulResponse(jobService.getDtoById(id));
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @PutMapping("/{id}")
    public RestResponse closeJob(@PathVariable Long id,
                                 @RequestBody boolean isPerformed) {
        try {
            jobService.close(id, isPerformed);
            return RestResponse.generateSuccessfulResponse("was closed");
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @PostMapping
    public RestResponse createJob(@RequestBody JobDto jobDto) {
        try {
            jobService.create(jobDto);
            return RestResponse.generateSuccessfulResponse("Created");
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }
}
