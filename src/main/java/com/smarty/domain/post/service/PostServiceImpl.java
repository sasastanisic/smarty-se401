package com.smarty.domain.post.service;

import com.smarty.domain.post.entity.Post;
import com.smarty.domain.post.model.PostRequestDTO;
import com.smarty.domain.post.model.PostResponseDTO;
import com.smarty.domain.post.model.PostUpdateDTO;
import com.smarty.domain.post.repository.PostRepository;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.PostMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_EXISTS = "Post with id %d doesn't exist";

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }

    @Override
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        Post post = postMapper.toPost(postRequestDTO);

        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        return postMapper.toPostResponseDTO(post);
    }

    @Override
    public Page<PostResponseDTO> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable).map(postMapper::toPostResponseDTO);
    }

    @Override
    public PostResponseDTO getPostById(Long id) {
        return postMapper.toPostResponseDTO(getById(id));
    }

    private Post getById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException(POST_NOT_EXISTS.formatted(id)));
    }

    @Override
    public List<PostResponseDTO> getLatestPosts() {
        List<Post> latestPosts = postRepository.findByOrderByCreatedAtDesc();

        if (latestPosts.isEmpty()) {
            throw new NotFoundException("List of latest posts is empty");
        }

        return latestPosts
                .stream()
                .limit(2)
                .map(postMapper::toPostResponseDTO)
                .toList();
    }

    @Override
    public PostResponseDTO updatePost(Long id, PostUpdateDTO postUpdateDTO) {
        Post post = getById(id);
        postMapper.updatePostFromDTO(postUpdateDTO, post);

        post.setCreatedAt(LocalDateTime.now());
        postRepository.save(post);

        return postMapper.toPostResponseDTO(post);
    }

    @Override
    public void deletePost(Long id) {
        if (!postRepository.existsById(id)) {
            throw new NotFoundException(POST_NOT_EXISTS.formatted(id));
        }

        postRepository.deleteById(id);
    }

}
