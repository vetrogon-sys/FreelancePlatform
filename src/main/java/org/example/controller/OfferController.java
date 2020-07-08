package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.RestResponse;
import org.example.exceptions.FailedRequestError;
import org.example.service.OfferService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs/{jobId}/offers")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService offerService;

    @PutMapping("/{id}")
    public RestResponse confirmOffer(@PathVariable Long id, @PathVariable Long jobId) {
        try {
            offerService.confirm(jobId, id);
            return RestResponse.generateSuccessfulResponse("Confirmed");
        } catch (FailedRequestError error) {
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @GetMapping
    public RestResponse getOffers(@PathVariable Long jobId) {
        try {
            return RestResponse.generateSuccessfulResponse(offerService.getDtoList(jobId));
        } catch (FailedRequestError error){
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @GetMapping("/{id}")
    public RestResponse getOfferById(@PathVariable Long id, @PathVariable Long jobId) {
        try {
            return RestResponse.generateSuccessfulResponse(offerService.getDtoById(jobId, id));
        } catch (FailedRequestError error){
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }

    @PostMapping()
    public RestResponse createOffer(@PathVariable Long jobId) {
        try {
            offerService.create(jobId);
            return RestResponse.generateSuccessfulResponse("Created");
        } catch (FailedRequestError error){
            return RestResponse.generateFailedResponse(error.getMessage());
        }
    }
}
