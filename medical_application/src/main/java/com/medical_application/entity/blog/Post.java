package com.medical_application.entity.blog;



import com.medical_application.entity.Doctor;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="posts",uniqueConstraints = {@UniqueConstraint(columnNames ={"title"})})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="title",nullable = false)
    private String title;
    @Column(name="description",nullable = false)
    private String description;
    @Column(name="content",nullable = false)
    private String content;
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    // Add a reference to Doctor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

}

