package com.medical_application.service.impl.blog;

import com.medical_application.entity.blog.Comment;
import com.medical_application.entity.blog.Post;
import com.medical_application.exception.BlogApiException;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.payload.blog.CommentDto;
import com.medical_application.repository.blog_repository.CommentRepository;
import com.medical_application.repository.blog_repository.PostRepository;
import com.medical_application.service.blog.CommentService;
import com.medical_application.util.EntityDtoMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository PostRepo;
    private final EntityDtoMapper entityDtoMapper;
    public CommentServiceImpl(CommentRepository commentRepo, PostRepository postRepo, EntityDtoMapper entityDtoMapper) {
        this.commentRepo = commentRepo;
        PostRepo = postRepo;
        this.entityDtoMapper = entityDtoMapper;
    }


    @Override
    public CommentDto createComment(long postId, Comment comment) {
        Post post = PostRepo.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post Not Found With id: " + postId));
        comment.setPost(post);
        Comment savedComment = commentRepo.save(comment);
        CommentDto commentDto = entityDtoMapper.mapToDto(savedComment, CommentDto.class);
        return commentDto;
    }

    @Override
    public List<CommentDto> findByPostId(long postId) {
        Post post = PostRepo.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post Not Found With Id " + postId));
        List<Comment> entityComments = commentRepo.findByPostId(postId);
        List<CommentDto> commentDtos = entityComments.stream().map(comment->entityDtoMapper.mapToDto(comment, CommentDto.class)).collect(Collectors.toList());
        return commentDtos ;
    }
    @Override
    public CommentDto getCommentById(long postId, long commentId) {
        Post post = PostRepo.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post Not Found With Id " + postId));
        Comment comment = commentRepo.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment Not Found with Id " + commentId));
        if(comment.getPost().getId()!=(post.getId())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST,"Comment does not belong to Post");
        }
        CommentDto dto = entityDtoMapper.mapToDto(comment, CommentDto.class);
        return dto;
    }
    @Override
    public CommentDto updateComment(long postId, CommentDto commentDto, long commentId) {
        Post post = PostRepo.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post Not Found With Id " + postId));
        Comment comment = commentRepo.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment Not Found with Id " + commentId));
        if(comment.getPost().getId()!=(post.getId())){
            throw new BlogApiException(HttpStatus.BAD_REQUEST,"Comment does not belong to Post");
        }
        comment.setName(commentDto.getName());
        comment.setEmail(commentDto.getEmail());
        comment.setBody(commentDto.getBody());
        Comment savedComment = commentRepo.save(comment);
        CommentDto dto = entityDtoMapper.mapToDto(savedComment, CommentDto.class);
        return dto;
    }
    @Override
    public void deleteComment(long postId, long commentId) {
        Post post = PostRepo.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post Not Found With I " + postId));
        Comment comment = commentRepo.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Comment Not Found with Id " + commentId));
        if (comment.getPost().getId() != (post.getId())) {
            throw new BlogApiException(HttpStatus.BAD_REQUEST,"Comment does not belong to Post");
        }
        commentRepo.delete(comment);
    }
    @Override
    public List<CommentDto> getAllComments() {
        List<Comment> comments = commentRepo.findAll();
        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> entityDtoMapper.mapToDto(comment, CommentDto.class)).collect(Collectors.toList());
        return commentDtos ;
    }
}
