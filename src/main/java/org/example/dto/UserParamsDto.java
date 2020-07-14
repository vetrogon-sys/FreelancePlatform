package org.example.dto;

import org.example.entity.Skill;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserParamsDto {

    private String name;
    private String surname;
    private String imgSrc;
    private List<Skill> skills = new ArrayList<>();

}
