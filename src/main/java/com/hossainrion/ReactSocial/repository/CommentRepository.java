package com.hossainrion.ReactSocial.repository;

import com.hossainrion.ReactSocial.entity.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends CrudRepository<Comment, Long> {
}
