package com.smarty.web;

import com.smarty.domain.post.model.PostRequestDTO;
import com.smarty.domain.post.model.PostResponseDTO;
import com.smarty.domain.post.model.PostUpdateDTO;
import com.smarty.domain.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO postRequestDTO) {
        return ResponseEntity.ok(postService.createPost(postRequestDTO));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/latest")
    public ResponseEntity<List<PostResponseDTO>> getLatestPosts() {
        return ResponseEntity.ok(postService.getLatestPosts());
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @PutMapping("/{id}")
    public ResponseEntity<PostResponseDTO> updatePost(@PathVariable Long id, @Valid @RequestBody PostUpdateDTO postUpdateDTO) {
        return ResponseEntity.ok(postService.updatePost(id, postUpdateDTO));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long id) {
        postService.deletePost(id);
    }

}
