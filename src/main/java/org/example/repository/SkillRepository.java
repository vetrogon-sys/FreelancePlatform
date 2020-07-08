package org.example.repository;

import org.example.entity.Skill;
import org.springframework.data.repository.CrudRepository;

public interface SkillRepository extends CrudRepository<Skill, String> {
}
