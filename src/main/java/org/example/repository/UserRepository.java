package org.example.repository;

import org.example.entity.Freelancer;
import org.example.entity.Skill;
import org.example.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByLogin(String login);
    List<Freelancer> findBySkillsIn(List<Skill> skills, Pageable pageable);
    List<Freelancer> findByRating(Double rating, Pageable pageable);
}
