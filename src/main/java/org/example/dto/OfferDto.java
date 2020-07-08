package org.example.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferDto {

    private FreelancerDto freelancer;

    private JobDto job;
}
