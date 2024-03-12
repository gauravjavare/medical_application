package com.medical_application.payload.blog;

import lombok.Data;

@Data
public class CommentDto {
    private long id;
    private String name;
    private String email;
    private String body;
    private long postId;
}
