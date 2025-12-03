package com.hossainrion.ReactSocial.entity;

import com.hossainrion.ReactSocial.Util;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    @ManyToMany private Set<User> sentRequests;
    @ManyToMany private Set<User> friends;

    public String getPictureBase64() {
        String pictureBase64 = "";
        if (getPicture() != null && Util.pictureExists(getPicture())) {
            try {
                pictureBase64 = Util.imageUrlToBase64(getPicture());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return pictureBase64;
    }

    public User() {}
    public Long getId() {return id;}
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
    public Set<User> getSentRequests() {return sentRequests;}
    public void setSentRequests(Set<User> sentRequests) {this.sentRequests = sentRequests;}
    public Set<User> getFriends() {return friends;}
    public void setFriends(Set<User> friends) {this.friends = friends;}

    @Override
    public List<GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority("USER"));

        return auth;
    }
}