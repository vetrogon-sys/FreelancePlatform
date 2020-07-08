package org.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @JsonIgnoreProperties("jobs")
    private UserDto employer;
    @JsonIgnoreProperties("jobs")
    private UserDto freelancer;

    private Stage stage;
}
