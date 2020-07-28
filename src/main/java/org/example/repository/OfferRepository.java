package org.example.repository;

import org.example.entity.Job;
import org.example.entity.Offer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OfferRepository extends CrudRepository<Offer, Long> {

    List<Offer> findAllByJob(Job job);
}
