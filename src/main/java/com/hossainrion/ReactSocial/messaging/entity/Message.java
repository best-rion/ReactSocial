package com.hossainrion.ReactSocial.messaging.entity;

import java.util.Date;

import com.hossainrion.ReactSocial.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
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
    private User sender;
    @ManyToOne
    private User receiver;
    private int seen;
}
