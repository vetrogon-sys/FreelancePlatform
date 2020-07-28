package org.example.service;

import org.example.dto.FilterRequestDto;
import org.example.dto.JobDto;
import org.example.exceptions.FailedRequestError;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface JobService {
    List<JobDto> getDtoListByFilter(FilterRequestDto filterRequestDto, Pageable pageable);
    JobDto getDtoById(Long id) throws FailedRequestError;
    void create(JobDto jobDto) throws FailedRequestError;
    void close(Long id, boolean isPerformed) throws FailedRequestError;
}
