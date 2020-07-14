package org.example.repository;

import org.example.entity.Job;
import org.example.entity.Skill;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobRepository extends CrudRepository<Job, Long> {

    List<Job> findBySkillsIn(List<Skill> skills, Pageable pageable);
}
