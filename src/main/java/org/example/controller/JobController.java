package org.example.controller;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.example.dto.JobDto;
import org.example.dto.RestResponse;
import org.example.dto.UserDto;
import org.example.entity.Employer;
import org.example.entity.Job;
import org.example.entity.User;
import org.example.repository.JobRepository;
import org.example.repository.OfferRepository;
import org.example.repository.StageRepository;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {
    private final UserService userService;
    private final JobRepository jobRepository;
    private final OfferRepository offerRepository;
    private final StageRepository stageRepository;
    private MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    @GetMapping
    public RestResponse getJobs() {
        List<Job> jobs = StreamSupport.stream(jobRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        return RestResponse.builder()
                .isSuccess(true)
                .response(jobs)
                .build();
    }

    @GetMapping("/{id}")
    public RestResponse findJobByIid(@PathVariable Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        mapperFactory.classMap(User.class, UserDto.class);
        MapperFacade mapper = mapperFactory.getMapperFacade();

        JobDto jobDto = JobDto.builder()
                .name(job.getName())
                .description(job.getDescription())
                .employer(mapper.map(job.getEmployer(), UserDto.class))
                .skills(job.getSkills())
                .stage(job.getStage())
                .build();

        return RestResponse.builder()
                .isSuccess(true)
                .response(jobDto)
                .build();
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        User currentUser = userService.getUserFromSecurityContext();
        if (currentUser.getClass().equals(Employer.class)) {
            job.setStage(stageRepository.findById("Размещено").orElse(null));
            job.setEmployer((Employer) currentUser);
            Job savedJob = jobRepository.save(job);
            return ResponseEntity.created(URI.create("/jobs/" + savedJob.getId())).body("Created");
        }
        return ResponseEntity.badRequest().body("Only employer can create job");
    }


}
