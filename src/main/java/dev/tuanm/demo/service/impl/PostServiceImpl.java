package dev.tuanm.demo.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import dev.tuanm.demo.common.constant.AuthorityConstants;
import dev.tuanm.demo.common.constant.DataResultConstants;
import dev.tuanm.demo.common.exception.InternalServerException;
import dev.tuanm.demo.common.exception.NotFoundException;
import dev.tuanm.demo.common.exception.UnauthorizedRequestException;
import dev.tuanm.demo.model.entity.Post;
import dev.tuanm.demo.model.entity.User;
import dev.tuanm.demo.model.request.PostCreationRequest;
import dev.tuanm.demo.model.request.PostPaginationRequest;
import dev.tuanm.demo.model.response.PostInfoResponse;
import dev.tuanm.demo.repository.PostRepository;
import dev.tuanm.demo.repository.PostWriteRepository;
import dev.tuanm.demo.repository.UserRepository;
import dev.tuanm.demo.service.ContentGeneratingService;
import dev.tuanm.demo.service.PostService;
import dev.tuanm.demo.service.factory.ContentGeneratingServiceFactory;
import dev.tuanm.demo.utils.SecurityUtils;

@Service
public class PostServiceImpl implements PostService {

    private final SecurityUtils securityUtils;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostWriteRepository postWriteRepository;
    private final ContentGeneratingServiceFactory contentGeneratingServiceFactory;

    public PostServiceImpl(
            SecurityUtils securityUtils,
            PostRepository postRepository,
            UserRepository userRepository,
            PostWriteRepository postWriteRepository,
            ContentGeneratingServiceFactory contentGeneratingServiceFactory) {
        this.securityUtils = securityUtils;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postWriteRepository = postWriteRepository;
        this.contentGeneratingServiceFactory = contentGeneratingServiceFactory;
    }

    @Override
    public PostInfoResponse create(PostCreationRequest request) {
        Optional<User> user = Optional.ofNullable(securityUtils.getLoggedUsername())
                .flatMap(this.userRepository::findByUsername);
        if (user.isPresent()) {
            Post saved = this.postRepository.save(Post.builder()
                    .author(user.get())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .build());
            return this.toPostInfoResponse(saved);
        }
        return null;
    }

    @Override
    public void create(Collection<PostCreationRequest> posts) {
        Optional<User> user = Optional.ofNullable(this.securityUtils.getLoggedUsername())
                .flatMap(this.userRepository::findByUsername);
        if (user.isPresent() && !posts.isEmpty()) {
            this.postWriteRepository.saveAll(posts.stream()
                    .map(post -> Post.builder()
                            .author(user.get())
                            .title(post.getTitle())
                            .content(post.getContent())
                            .build())
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public PostInfoResponse view(Long postId) {
        return this.postRepository.findById(postId)
                .map(this::toPostInfoResponse)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Collection<PostInfoResponse> search(PostPaginationRequest request) {
        return this.postRepository.search(
                PageRequest.of(request.getPage(), request.getPageSize()),
                request.getPostId(),
                request.getTitle(),
                request.getAuthor()).stream()
                .map(this::toPostInfoResponse).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void remove(Long postId) {
        if (this.securityUtils.getLoggedAuthorities().contains(AuthorityConstants.ROLE_ADMIN)) {
            this.postRepository.deleteById(postId);
        } else {
            if (Optional.ofNullable(this.securityUtils.getLoggedUsername())
                    .map(username -> this.postRepository
                            .deleteByIdAndAuthor(postId, username) == DataResultConstants.MODIFIED_FAILED)
                    .orElse(true)) {
                throw new UnauthorizedRequestException();
            }
        }
    }

    @Override
    public Collection<String> generate(boolean async, int total, String generator) {
        return async
                ? this.generateUsingCompletableFuture(total, generator)
                : this.generateNormally(total, generator);
    }

    private Collection<String> generateNormally(int total, String generator) {
        ContentGeneratingService contentGeneratingService = contentGeneratingServiceFactory.getService(generator);
        List<String> generatedContents = new ArrayList<>();
        for (int count = 0; count < total; count++) {
            generatedContents.add(contentGeneratingService.generate());
        }
        return generatedContents;
    }

    @SuppressWarnings("java:S2142")
    private Collection<String> generateUsingCompletableFuture(int total, String generator) {
        ContentGeneratingService contentGeneratingService = contentGeneratingServiceFactory.getService(generator);
        List<CompletableFuture<String>> completableFutures = new ArrayList<>();
        for (int count = 0; count < total; count++) {
            completableFutures.add(CompletableFuture.supplyAsync(contentGeneratingService::generate));
        }
        try {
            return CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[total]))
                    .thenApply(
                            v -> completableFutures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                    .get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new InternalServerException(ex.getMessage());
        }
    }

    private PostInfoResponse toPostInfoResponse(Post post) {
        return PostInfoResponse.builder()
                .id(post.getId())
                .author(post.getAuthor().getUsername())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}
