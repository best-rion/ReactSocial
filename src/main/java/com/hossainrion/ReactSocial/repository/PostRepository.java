package com.hossainrion.ReactSocial.repository;

import com.hossainrion.ReactSocial.entity.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Integer> {
    Post findById(long id);
    List<Post> findAllByAuthorId(Long authorId);
}
