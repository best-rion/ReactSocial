package com.hossainrion.ReactSocial.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "post_table")
public class Post {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private User author;
    private Date createdAt;
    @Column(columnDefinition="TEXT")
    private String content;
    private String mediaFileName;
    private Long numberOfLikes = (long) 0;
    private Long numberOfComments = (long) 0;
}
