package com.medical_application.service.impl.blog;


import com.medical_application.entity.Doctor;
import com.medical_application.entity.blog.Post;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.payload.blog.PostDto;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.blog_repository.PostRepository;
import com.medical_application.service.blog.PostService;
import com.medical_application.util.EntityDtoMapper;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepo;

    private final DoctorRepository doctorRepo;

    private final EntityDtoMapper entityDtoMapper;
    public PostServiceImpl(DoctorRepository doctorRepo, PostRepository postRepo, EntityDtoMapper entityDtoMapper) {
        this.postRepo = postRepo;
        this.doctorRepo = doctorRepo;
        this.entityDtoMapper = entityDtoMapper;
    }

    @Override
    public PostDto savePost(Post post){
        // Retrieve the logged-in doctor's username (assuming the username is the email)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String doctorEmail = authentication.getName();

        // Fetch the doctor from the database
             Doctor doctor = doctorRepo.findByEmail(doctorEmail)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with email: " + doctorEmail));
             post.setDoctor(doctor);
             Post savedPost = postRepo.save(post);
          PostDto dto = new PostDto();
          dto.setId(savedPost.getId());
          dto.setTitle(savedPost.getTitle());
          dto.setDescription(savedPost.getDescription());
          dto.setContent(savedPost.getContent());
          dto.setDoctorId(doctor.getDoctorId());
        return dto;
    }



    @Override
    public void deletePost(long postId) {
        Post post = postRepo.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not foung with id " + postId));
        postRepo.deleteById(post.getId());
    }

    @Override
    public PostDto updatePost(long postId, Post post) {
        // Retrieve the logged-in doctor's username (assuming the username is the email)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String doctorEmail = authentication.getName();
        // Fetch the doctor from the database
        Doctor doctor = doctorRepo.findByEmail(doctorEmail)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with email: " + doctorEmail));
        Post post1 = postRepo.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post Not Found with Id " + postId));

        post1.setTitle(post.getTitle());
        post1.setDescription(post.getDescription());
        post1.setContent(post.getContent());
        post1.setDoctor(doctor);
        Post save = postRepo.save(post1);
        PostDto postDto = entityDtoMapper.mapToDto(save, PostDto.class);
        return postDto;
    }



    @Override
    public PostDto getPostById(long postId) {
        Post post = postRepo.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Post Not Found with id " + postId));
        PostDto dto = entityDtoMapper.mapToDto(post, PostDto.class);
        return dto;
    }

    @Override
    public List<PostDto> getAllPosts() {
        List<Post> posts = postRepo.findAll();
        List<PostDto> postDtos = posts.stream().map(post -> (entityDtoMapper.mapToDto(post, PostDto.class)))
                .collect(Collectors.toList());
        return postDtos;
    }










}
