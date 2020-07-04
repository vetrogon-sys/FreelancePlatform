package org.example.repository;

import org.example.entity.Offer;
import org.springframework.data.repository.CrudRepository;

public interface OfferRepository extends CrudRepository<Offer, Long> {
}
