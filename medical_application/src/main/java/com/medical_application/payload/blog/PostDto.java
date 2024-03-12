package com.medical_application.payload.blog;



import lombok.*;

import javax.validation.constraints.*;

@Data
public class PostDto {


    private long id;
    @NotEmpty
    @Size(min = 2,message = " Post title should contains at least 2 characters")
    private String title;
    @NotEmpty
    @Size(min = 6,message = "Post description should contains at least 6 characters")
    private String description;
    @NotEmpty
    @Size(min = 10,message = "Post content should contains at least 10 characters")
    private String content;

    private long doctorId;
}

