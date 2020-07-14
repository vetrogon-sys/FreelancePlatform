package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.FilterRequestDto;
import org.example.dto.JobDto;
import org.example.dto.RestResponse;
import org.example.exceptions.FailedRequestError;
import org.example.service.JobService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @GetMapping
    public RestResponse getJobs(@RequestBody(required = false) FilterRequestDto filterRequestDto,
                                @PageableDefault(page = 0, size = 20)
                                @SortDefault(sort = "id", direction = Sort.Direction.ASC)
                                        Pageable pageable) {
        return RestResponse.generateSuccessfulResponse(jobService.getDtoListByFilter(filterRequestDto, pageable));
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
    @PreAuthorize("hasRole('EMPLOYER')")
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
    @PreAuthorize("hasRole('EMPLOYER')")
    public RestResponse createJob(@RequestBody JobDto jobDto) {
        try {
            jobService.create(jobDto);
            return RestResponse.generateSuccessfulResponse("Created");
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }
}
