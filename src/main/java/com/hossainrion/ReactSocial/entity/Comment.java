package com.hossainrion.ReactSocial.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @ManyToOne
    private User author;
    @Column(columnDefinition="TEXT")
    private String content;
    private Date date;
}
