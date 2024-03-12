package com.medical_application.controller.blog;

import com.medical_application.entity.blog.Comment;
import com.medical_application.payload.blog.CommentDto;
import com.medical_application.service.blog.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
@PostMapping("/{postId}")
 public ResponseEntity<?>createComment(@PathVariable("postId")long postId, @RequestBody Comment comment){
    CommentDto dto = commentService.createComment(postId, comment);
    return new ResponseEntity<>(dto, HttpStatus.CREATED);
}
@GetMapping("/findBy/{postId}")
public ResponseEntity<List<CommentDto>>findBYPostId(@PathVariable("postId")long postId){
    List<CommentDto> commentDtos = commentService.findByPostId(postId);
    return new ResponseEntity<>(commentDtos,HttpStatus.OK);
}
@GetMapping("/postId/{postId}/commentId/{commentId}")
public ResponseEntity<CommentDto>getCommentById(@PathVariable("postId")long postId,
                                                @PathVariable("commentId")long commentId){
    CommentDto commentDto = commentService.getCommentById(postId, commentId);
    return new ResponseEntity<>(commentDto,HttpStatus.OK);
}
@GetMapping("/getAllComments")
public ResponseEntity<List<CommentDto>>getAllComments(){
    List<CommentDto> commentDtos = commentService.getAllComments();
    return new ResponseEntity<>(commentDtos,HttpStatus.OK );
}
@PutMapping("/postId/{postId}/commentId/{commentId}")
public ResponseEntity<CommentDto>updateComment(@PathVariable("postId")long postId,
                                               @RequestBody CommentDto commentDto,
                                               @PathVariable("commentId")long commentId){
    CommentDto dto = commentService.updateComment(postId, commentDto, commentId);
    return new ResponseEntity<>(dto,HttpStatus.OK);
}
@DeleteMapping("/postId/{postId}/commentId/{commentId}")
public ResponseEntity<String>deleteComment(@PathVariable("postId")long postId,
                                           @PathVariable("commentId")long commentId){
        commentService.deleteComment(postId,commentId);

        return new ResponseEntity<>("Comment is Deleted",HttpStatus.OK);
}

}
