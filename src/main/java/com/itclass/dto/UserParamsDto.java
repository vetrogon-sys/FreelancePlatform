package com.itclass.dto;

import com.itclass.entity.Skill;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserParamsDto {

    private String name;
    private String surname;
    private List<Skill> skills = new ArrayList<>();
}
