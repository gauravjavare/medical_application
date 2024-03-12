package com.medical_application.payload.review;

import lombok.Data;


@Data
public class ReviewDto {

    private long reviewId;
    private long doctorId;
    private long userId;
    private int rating;
    private String description;
}
