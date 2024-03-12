package com.medical_application.service.blog;

import com.medical_application.entity.blog.Comment;
import com.medical_application.payload.blog.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(long postId, Comment comment);

    public List<CommentDto> findByPostId(long postId);

    public CommentDto getCommentById(long postId, long commentId);

    public CommentDto updateComment(long postId, CommentDto commentDto, long commentId);

  public  void deleteComment(long postId, long commentId);

    public List<CommentDto> getAllComments();
}
