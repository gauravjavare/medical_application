package com.medical_application.controller.review;


import com.medical_application.entity.review.Review;
import com.medical_application.payload.review.ReviewDto;
import com.medical_application.service.review.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @PostMapping
    public ResponseEntity<ReviewDto> saveReview(@RequestBody Review review,
                                                @RequestParam long doctorId) {
        ReviewDto createdReviewDto = reviewService.createReview(review, doctorId);
        return new ResponseEntity<>(createdReviewDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok("Review is deleted");
    }


}

