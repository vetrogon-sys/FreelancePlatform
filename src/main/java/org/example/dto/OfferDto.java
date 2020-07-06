package org.example.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OfferDto {

    private UserDto freelancer;

    private Long jobId;
}
