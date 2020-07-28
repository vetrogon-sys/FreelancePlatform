package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.config.OrikaConfig;
import org.example.dto.FilterRequestDto;
import org.example.dto.JobDto;
import org.example.entity.Employer;
import org.example.entity.Job;
import org.example.entity.Stage;
import org.example.entity.User;
import org.example.exceptions.FailedRequestError;
import org.example.repository.JobRepository;
import org.example.service.JobService;
import org.example.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final UserService userService;

    @Override
    public List<JobDto> getDtoListByFilter(FilterRequestDto filterRequestDto, Pageable pageable) {
        List<Job> jobs;

        if (filterRequestDto.getSkills() == null) {
            jobs = jobRepository.findAllByStage(Stage.POSTED, pageable);
        } else {
            jobs = jobRepository.findBySkillsInAndStage(filterRequestDto.getSkills(), Stage.POSTED, pageable);
        }

        return OrikaConfig.getMapperFacade()
                .mapAsList(jobs, JobDto.class);
    }


    @Override
    public JobDto getDtoById(Long id) throws FailedRequestError {
        Job job = jobRepository.findById(id).orElse(null);
        if (job != null) {
            return OrikaConfig
                    .getMapperFacade()
                    .map(job, JobDto.class);
        } else {
            throw new FailedRequestError("There is not job with same id");
        }
    }

    @Override
    public void create(JobDto jobDto) throws FailedRequestError {
        User currentUser = userService.getUserFromSecurityContext();
        if (currentUser == null
                || !Employer.class.equals(currentUser.getClass())) {
            throw new FailedRequestError("only employer can create job");
        }

        Job job = OrikaConfig
                .getMapperFacade()
                .map(jobDto, Job.class);
        job.setStage(Stage.POSTED);
        job.setEmployer((Employer) currentUser);
        job.setCreatedOn(LocalDateTime.now().withNano(0));
        jobRepository.save(job);
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
