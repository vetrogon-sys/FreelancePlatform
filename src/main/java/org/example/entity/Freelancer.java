package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "freelancers")
public class Freelancer extends User {

    @ManyToMany
    @JoinTable(name = "freelancer_skills",
            joinColumns = @JoinColumn(name = "id"),
            inverseJoinColumns = @JoinColumn(name = "name"))
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "freelancer", fetch = FetchType.LAZY)
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(mappedBy = "freelancer", fetch = FetchType.LAZY)
    private List<Job> jobs = new ArrayList<>();
}
