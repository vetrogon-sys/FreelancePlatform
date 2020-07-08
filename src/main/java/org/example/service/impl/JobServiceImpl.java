package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.config.OrikaConfig;
import org.example.dto.JobDto;
import org.example.entity.*;
import org.example.exceptions.FailedRequestError;
import org.example.repository.JobRepository;
import org.example.service.JobService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final UserService userService;

    @Override
    public List<JobDto> getDtoList() {
        List<Job> jobs = StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        return jobs.stream()
                .map(e -> OrikaConfig.getMapperFactory()
                        .getMapperFacade()
                        .map(e, JobDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<JobDto> getDtoListBySkills() {
        List<Job> jobs = StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        List<Job> filteredJobs = new ArrayList<>();
        Freelancer freelancer = (Freelancer) userService.getUserFromSecurityContext();
        List<Skill> skillList = freelancer.getSkills();

        if (skillList != null) {
            filteredJobs = jobs.stream()
                    .filter(e -> !Collections.disjoint(e.getSkills(), skillList))
                    .collect(Collectors.toList());
        }

        return filteredJobs.stream()
                .map(e -> OrikaConfig.getMapperFactory()
                        .getMapperFacade()
                        .map(e, JobDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public JobDto getDtoById(Long id) throws FailedRequestError {
        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            return OrikaConfig.getMapperFactory()
                    .getMapperFacade()
                    .map(job, JobDto.class);
        } else {
            throw new FailedRequestError("There is not job with same id");
        }
    }

    @Override
    public void create(JobDto jobDto) throws FailedRequestError {
        User currentUser = userService.getUserFromSecurityContext();
        if (currentUser.getClass().equals(Employer.class)) {
            Job job = OrikaConfig.getMapperFactory()
                    .getMapperFacade()
                    .map(jobDto, Job.class);
            job.setStage(Stage.POSTED);
            job.setEmployer((Employer) currentUser);
            jobRepository.save(job);
        } else {
            throw new FailedRequestError("only employer can create job");
        }
    }

    @Override
    public void close(Long id, boolean isPerformed) throws FailedRequestError {
        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            if (isPerformed) {
                job.setStage(Stage.COMPLETED);
            } else {
                job.setStage(Stage.DECLINED);
            }
            jobRepository.save(job);
        } else {
            throw new FailedRequestError("There is not job with same id");
        }
    }
}
