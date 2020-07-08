package org.example.service;

import org.example.dto.JobDto;
import org.example.exceptions.FailedRequestError;

import java.util.List;

public interface JobService {
    List<JobDto> getDtoList();
    List<JobDto> getDtoListBySkills();
    JobDto getDtoById(Long id) throws FailedRequestError;
    void create(JobDto jobDto) throws FailedRequestError;
    void close(Long id, boolean isPerformed) throws FailedRequestError;
}
