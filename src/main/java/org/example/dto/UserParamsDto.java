package org.example.dto;

import lombok.Data;
import org.example.entity.Skill;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserParamsDto {
    private String name;
    private String surname;
    private String fileSrc;
    private List<Skill> skills = new ArrayList<>();
}
