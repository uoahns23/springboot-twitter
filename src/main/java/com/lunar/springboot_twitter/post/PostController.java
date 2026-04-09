package com.lunar.springboot_twitter.post;


import com.lunar.springboot_twitter.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostRepository postRepository;

    // 게시글 목록 조회
    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();

    }

    // 특정 게시글 조회(경로 변수 받기)
    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow();
    }

    // 새 게시글 작성
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Post createPost(@RequestBody Post post) {
        Post newPost = Post.builder()
                .content(post.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postRepository.save(newPost);

        return newPost;
    }

    //
    @PutMapping("/{id}")
    public Post updatePost(@PathVariable Long id, @RequestBody Post postRequest) {
        return postRepository.findById(id)
                .map(post -> {
                    post.updateContent(postRequest.getContent());
                    return postRepository.save(post);
                })
                .orElseThrow();

    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {

        postRepository.deleteById(id);
    }


    // 게시글 검색
    //  /api/posts/search?page=1&size=3
    @GetMapping("/search")
    public Slice<Post> searchPosts(
            @RequestParam(required = false) Long lastPostId,
            @RequestParam(defaultValue = "3") int size
    ) {
        int page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (lastPostId == null) {
            return postRepository.findSlicesBy(pageable);
        } else {
            return postRepository.findSlicesByIdLessThan(lastPostId, pageable);
        }
    }
}

