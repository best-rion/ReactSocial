package com.hossainrion.ReactSocial.repository;

import com.hossainrion.ReactSocial.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Integer>
{
    User findById(Long id);
    User findByUsername(String username);
    List<User> findAll();
    @Query(value = "SELECT * FROM user_table WHERE id IN (SELECT sender_id FROM friend_requests WHERE requested_user_id = :id)",nativeQuery = true)
    List<User> findReceivedRequestsById(Long id);
}