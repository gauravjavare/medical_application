package com.medical_application.entity;

import com.medical_application.entity.blog.Post;
import com.medical_application.entity.review.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long doctorId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private long mobile;

    @Column(name = "specialization", nullable = false)
    private String specialization;

    @Column(name = "experience")
    private Integer experience; // in years

    @Column(name = "qualification")
    private String qualification;


    @Column(name = "working_hours")
    private String workingHours;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "status")
    private String status;

    @Column(name = "hospital_clinic")
    private String hospitalOrClinic;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Appointment> appointments;
    private String password;
    private String role;
    // Define the relationship between Doctor and Post
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Post> posts = new HashSet<>();
    //store reviews
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();
}
