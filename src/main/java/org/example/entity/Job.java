package org.example.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    private Stage stage;

    @Column(name = "create_on")
    private LocalDateTime createdOn;

    @OneToMany(mappedBy = "job")
    private List<Offer> offers = new ArrayList<>();

}
