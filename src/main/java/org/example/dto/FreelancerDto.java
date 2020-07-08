package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.example.entity.Skill;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class FreelancerDto extends UserDto {

    private List<Skill> skills = new ArrayList<>();
}
