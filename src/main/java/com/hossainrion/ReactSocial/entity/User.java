package com.hossainrion.ReactSocial.entity;

import com.hossainrion.ReactSocial.utils.Util;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="user_table")
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column(unique=true)
    private String username;
    private String password;
    private String fullName;
    private String bio;
    private String picture;
    @ManyToMany
    @JoinTable(
            name = "friend_requests",                     // join table name
            joinColumns = @JoinColumn(
                    name = "sender_id",                // column in join table referencing THIS entity
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "requested_user_id",                // column in join table referencing OTHER entity
                    referencedColumnName = "id"
            )
    )
    private Set<User> sentRequests;
    @ManyToMany
    private Set<User> friends;

    public String getPictureBase64() {
        return Util.imageUrlToBase64(getPicture());
    }

    public void setPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.password = encoder.encode(password);
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority("USER"));

        return auth;
    }
}