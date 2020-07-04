package org.example.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class OfferDto {

    private UserDto freelancer;

    private JobDto job;
}
