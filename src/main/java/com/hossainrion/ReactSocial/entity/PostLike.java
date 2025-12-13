package com.hossainrion.ReactSocial.entity;

import com.hossainrion.ReactSocial.entity.composityKey.PostLikeId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "post_likes")
public class PostLike {

    @EmbeddedId
    private PostLikeId id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("postId")
    private Post post;

    public PostLike(User user, Post post) {
        this.user = user;
        this.post = post;
        this.id = new PostLikeId(user.getId(), post.getId());
    }
}

