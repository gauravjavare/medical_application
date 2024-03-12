package com.medical_application.service.impl.review;

import com.medical_application.entity.Doctor;
import com.medical_application.entity.User;
import com.medical_application.entity.review.Review;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.exception.UnauthorizedAccessException;
import com.medical_application.payload.review.ReviewDto;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.UserRepository;
import com.medical_application.repository.reveiw.ReviewRepository;
import com.medical_application.service.review.ReviewService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepo;
    private final DoctorRepository doctorRepo;
    private final UserRepository userRepo;
    public ReviewServiceImpl(ReviewRepository reviewRepo, DoctorRepository doctorRepo, UserRepository userRepo) {
        this.reviewRepo = reviewRepo;
        this.doctorRepo = doctorRepo;
        this.userRepo = userRepo;
    }

    @Override
    public ReviewDto createReview(Review review, long doctorId) {
        long loggedInUserId = getLoggedInUserId();
        User user = userRepo.findById(loggedInUserId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id " + loggedInUserId));
        Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(
                () -> new EntityNotFoundException("doctor not found with id " + doctorId));
        review.setUser(user);
        review.setDoctor(doctor);
        Review saveReview = reviewRepo.save(review);
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(saveReview.getReviewId());
        dto.setUserId(saveReview.getUser().getUserId());
        dto.setDoctorId(saveReview.getDoctor().getDoctorId());
        dto.setRating(saveReview.getRating());
        dto.setDescription(saveReview.getDescription());
        return dto;
    }

    @Override
    public void deleteReview(long reviewId) {
        // Fetch the currently authenticated user's ID
        long loggedInUserId = getLoggedInUserId();

        Review review = reviewRepo.findById(reviewId).orElseThrow(
                () -> new EntityNotFoundException("Review not found with id " + reviewId));

        // Check if the user attempting to delete the review is the one who created it
        if (review.getUser().getUserId() != loggedInUserId) {
            throw new UnauthorizedAccessException("User is not authorized to delete this review.");
        }
        reviewRepo.deleteById(reviewId);
    }

    // Helper method to get the ID of the currently authenticated user
    private long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepo.findByEmail(username).map(User::getUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + username));
    }
}
