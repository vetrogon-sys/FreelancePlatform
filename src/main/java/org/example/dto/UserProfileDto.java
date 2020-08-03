package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Skill;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserProfileDto {
    private String login;
    private String name;
    private String surname;
    private LocalDateTime createdOn;
    private byte[] image;
    private List<Skill> skills;
    private List<JobDto> jobs;
    private List<ReviewDto> reviews;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        UserProfileDto that = (UserProfileDto) object;
        return Objects.equals(login, that.login);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login);
    }
}