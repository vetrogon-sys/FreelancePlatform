package org.example.service;

import org.example.dto.OfferDto;
import org.example.exceptions.FailedRequestError;

import java.util.List;

public interface OfferService {
    void confirm(Long jobId, Long offerId) throws FailedRequestError;
    OfferDto getDtoById(Long jobId, Long offerId) throws FailedRequestError;
    List<OfferDto> getDtoList(Long jobId) throws FailedRequestError;
    void create(Long jobId) throws FailedRequestError;
}
