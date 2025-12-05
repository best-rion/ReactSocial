package com.hossainrion.ReactSocial.repository;

import com.hossainrion.ReactSocial.entity.PostLike;
import com.hossainrion.ReactSocial.entity.composityKey.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    boolean existsByUserIdAndPostId(long userId, long postId);
}
