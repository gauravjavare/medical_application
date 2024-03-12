
package com.medical_application.service.blog;


import com.medical_application.entity.blog.Post;
import com.medical_application.payload.blog.PostDto;

import java.util.List;

public interface PostService {

    public PostDto savePost(Post post);


    void deletePost(long postId);

    PostDto updatePost(long postId, Post post);

    PostDto getPostById(long id);

    List<PostDto> getAllPosts();
}
