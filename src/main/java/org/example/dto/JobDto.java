package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private UserDto freelancer;
    private Stage stage;
}
