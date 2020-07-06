package org.example.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.entity.Skill;
import org.example.entity.Stage;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class JobDto {
    private String name;
    private String description;

    private List<Skill> skills;

    private UserDto employer;

    private Stage stage;
}
