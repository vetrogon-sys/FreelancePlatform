package org.example.repository;

import org.example.entity.State;
import org.springframework.data.repository.CrudRepository;

public interface StateRepository extends CrudRepository<State, String> {
}
