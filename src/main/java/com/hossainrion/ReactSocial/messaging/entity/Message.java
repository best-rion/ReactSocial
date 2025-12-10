package com.hossainrion.ReactSocial.messaging.entity;

import java.util.Date;

import com.hossainrion.ReactSocial.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    @ManyToOne
    @NonNull
    private User sender;
    @ManyToOne
    @NonNull
    private User receiver;
    private int seen;
}
