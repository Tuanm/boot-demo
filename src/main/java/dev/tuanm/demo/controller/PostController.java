package dev.tuanm.demo.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import dev.tuanm.demo.common.constant.CachingConstants;
import dev.tuanm.demo.common.constant.PaginationContants;
import dev.tuanm.demo.common.constant.PathConstants;
import dev.tuanm.demo.model.request.PostCreationRequest;
import dev.tuanm.demo.model.request.PostPaginationRequest;
import dev.tuanm.demo.model.response.PostInfoResponse;
import dev.tuanm.demo.service.PostService;
import dev.tuanm.demo.utils.CSV;

@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Cacheable(key = "#postId", unless = "#result == null", cacheNames = CachingConstants.POST_VIEW_CACHE_NAME)
    @GetMapping(PathConstants.API_POSTS_VIEW_URL)
    public PostInfoResponse view(@PathVariable @NotNull Long postId) {
        return postService.view(postId);
    }

    @Cacheable(key = "{ #title, #author, #page, #pageSize }", cacheNames = CachingConstants.POST_SEARCH_CACHE_NAME)
    @GetMapping(PathConstants.API_POSTS_SEARCH_URL)
    public Collection<PostInfoResponse> search(
            @RequestParam(name = "post_id", required = false) Long postId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer page,
            @RequestParam(name = "page_size", required = false) Integer pageSize) {
        return postService.search(PostPaginationRequest.builder()
                .postId(postId)
                .title(title)
                .author(author)
                .page(Optional.ofNullable(page).orElse(0))
                .pageSize(Optional.ofNullable(pageSize).orElse(PaginationContants.DEFAULT_PAGE_SIZE)).build());
    }

    @CachePut(key = "#result.id", cacheNames = CachingConstants.POST_VIEW_CACHE_NAME)
    @PostMapping(PathConstants.API_POSTS_CREATION_URL)
    public PostInfoResponse create(@RequestBody @NotNull PostCreationRequest request) {
        return postService.create(request);
    }

    @CacheEvict(key = "#postId", cacheNames = CachingConstants.POST_VIEW_CACHE_NAME)
    @DeleteMapping(PathConstants.API_POSTS_DELETION_URL)
    public void remove(@PathVariable @NotNull Long postId) {
        postService.remove(postId);
    }

    @PostMapping(PathConstants.API_POSTS_UPLOAD_URL)
    public void upload(@RequestParam MultipartFile file) throws IOException {
        CSV csv = CSV.builder()
                .hasHeader(true)
                .delimiter(",")
                .build()
                .fromText(new String(file.getBytes()));
        CSV.HeaderValueEntityMapper<PostCreationRequest> mapper = post -> (header, value) -> {
            if (header.equalsIgnoreCase("title")) {
                post.setTitle(value);
            } else if (header.equalsIgnoreCase("content")) {
                post.setContent(value);
            }
        };
        postService.create(csv.as(PostCreationRequest::new, mapper));
    }

    @GetMapping(PathConstants.API_POSTS_GENERATE_URL)
    public Collection<String> generate(
        @RequestParam(required = false, defaultValue = "true") Boolean async,
        @RequestParam(required = false) String generator,
        @RequestParam(required = false, defaultValue = "1") Integer total
    ) {
        return postService.generate(async, total.intValue(), generator);
    }
}
