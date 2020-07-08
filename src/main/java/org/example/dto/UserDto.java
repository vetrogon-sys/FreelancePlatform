package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String login;
    private String name;
    private String surname;
    private LocalDateTime createdOn;

    private List<Skill> skills;
    private List<JobDto> jobs;
}