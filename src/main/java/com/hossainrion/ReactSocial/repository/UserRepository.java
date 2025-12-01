package com.hossainrion.ReactSocial.repository;

import com.hossainrion.ReactSocial.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User,Integer>
{
    User findByEmail(String email);
    List<User> findAll();
}