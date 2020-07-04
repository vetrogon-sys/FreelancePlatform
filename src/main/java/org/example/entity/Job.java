package org.example.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @OneToMany()
    @JoinColumn(name = "skills_name")
    private List<Skill> skills = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @ManyToOne()
    @JoinColumn(name = "stage_name")
    private Stage stage;

    @OneToMany(mappedBy = "job")
    private List<Offer> offers = new ArrayList<>();
}
