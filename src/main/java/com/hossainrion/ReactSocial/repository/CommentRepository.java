package com.hossainrion.ReactSocial.repository;

import com.hossainrion.ReactSocial.entity.Comment;
import com.hossainrion.ReactSocial.entity.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByPostId(Long postId);
    void deleteAllByPost(Post post);
}
