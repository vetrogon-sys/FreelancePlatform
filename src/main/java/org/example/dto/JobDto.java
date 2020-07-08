package org.example.dto;

import lombok.*;
import org.example.entity.Skill;
import org.example.entity.Stage;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDto {
    private String name;
    private String description;

    private List<Skill> skills;

    private UserDto employer;
    private FreelancerDto freelancer;

    private Stage stage;
}
