package com.medical_application.repository.blog_repository;

import com.medical_application.entity.blog.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    public List<Comment> findByPostId(long postId);
}
