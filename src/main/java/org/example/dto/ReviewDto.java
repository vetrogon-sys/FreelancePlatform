package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {

    private String title;
    private String message;
    private Double score;
    private UserDto employer;
    private UserDto freelancer;
}
