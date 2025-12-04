package com.hossainrion.ReactSocial.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Media
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    private MediaType type;
    private String fileName;
}