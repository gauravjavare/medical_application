package com.medical_application.controller.blog;


import com.medical_application.entity.blog.Post;
import com.medical_application.payload.blog.PostDto;
import com.medical_application.service.blog.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PostMapping
    public ResponseEntity<?> savePost(@Valid @RequestBody Post post, BindingResult result){
        if(result.hasErrors()){
            return new ResponseEntity<>(result.getFieldError().getDefaultMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        PostDto dto = postService.savePost(post);
        return new ResponseEntity<>(dto,HttpStatus.CREATED) ;
    }
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @DeleteMapping("{postId}")
    public ResponseEntity<String>deletePost(@PathVariable long postId){
        postService.deletePost(postId);
        return new ResponseEntity<>("Post is Deleted",HttpStatus.OK);
    }
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @PutMapping("/{postId}")
    public ResponseEntity<PostDto>updatePost(@PathVariable long postId,@RequestBody  Post post ){
        PostDto dto = postService.updatePost(postId, post);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto>getPostById(@PathVariable long postId){
        PostDto dto = postService.getPostById(postId);
        return new ResponseEntity<>(dto,HttpStatus.OK);
    }

    @GetMapping("/getAllPosts")
    public ResponseEntity<List<PostDto>>getAllPost(){
        List<PostDto> posts = postService.getAllPosts();
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }


}
