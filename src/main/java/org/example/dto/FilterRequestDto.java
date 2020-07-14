package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Skill;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequestDto {
    private List<Skill> skills;
    private Double rating;
}
