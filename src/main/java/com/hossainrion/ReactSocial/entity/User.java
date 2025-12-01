package com.hossainrion.ReactSocial.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user_table")
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column(unique=true)
    private String email;
    private String password;
    private String fullName;
    private String bio;
    private String picture;

    public User() {}

    public String getUsername() {
        return getEmail();
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
    public String getPicture() {
        return picture;
    }
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        // TODO Auto-generated method stub

        List<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority("USER"));

        return auth;
    }
}