package dev.tuanm.demo.service;

import java.util.Collection;

import dev.tuanm.demo.model.request.PostCreationRequest;
import dev.tuanm.demo.model.request.PostPaginationRequest;
import dev.tuanm.demo.model.response.PostInfoResponse;

public interface PostService {
    /**
     * Creates a new post.
     *
     * @param request the post's info.
     */
    PostInfoResponse create(PostCreationRequest request);

    /**
     * Creates a number of posts.
     *
     * @param posts the collection of posts.
     */
    void create(Collection<PostCreationRequest> posts);

    /**
     * Views a post by its id.
     *
     * @param postId the post's id.
     */
    PostInfoResponse view(Long postId);

    /**
     * Searches some posts based on the user's filters.
     *
     * @param request contains the user's filters.
     */
    Collection<PostInfoResponse> search(PostPaginationRequest request);

    /**
     * Removes a post by its id.
     *
     * @param postId the post's id.
     */
    void remove(Long postId);

    /**
     * Generates a number of contents posting.
     *
     * @param total the number of contents to generate.
     * @param generator the generator's name.
     */
    Collection<String> generate(boolean async, int total, String generator);
}
