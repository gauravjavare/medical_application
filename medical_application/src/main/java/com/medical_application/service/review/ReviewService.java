package com.medical_application.service.review;


import com.medical_application.entity.review.Review;
import com.medical_application.payload.review.ReviewDto;

public interface ReviewService {

    ReviewDto createReview(Review review, long doctorId);

    void deleteReview(long reviewId);
}
