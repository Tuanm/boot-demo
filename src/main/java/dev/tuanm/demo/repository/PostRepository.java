package dev.tuanm.demo.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import dev.tuanm.demo.model.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
        @Query(nativeQuery = true, value = " SELECT p.* FROM posts p "
                        + " LEFT JOIN users u ON p.author = u.username "
                        + " WHERE (ISNULL(:postId) OR p.id = :postId) "
                        + " AND (ISNULL(:title) OR p.title LIKE %:title%) "
                        + " AND (ISNULL(:author) OR p.author = :author) ")
        List<Post> search(
                        Pageable pageable,
                        @Param("postId") Long postId,
                        @Param("title") String title,
                        @Param("author") String author);

        @Modifying
        @Query(value = " DELETE FROM Post p WHERE p.id = :postId AND p.author.username = :author ")
        int deleteByIdAndAuthor(
                        @Param("postId") Long postId,
                        @Param("author") String author);
}
