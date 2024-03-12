package com.medical_application.repository.blog_repository;


import com.medical_application.entity.blog.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {



}
