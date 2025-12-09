package com.hossainrion.ReactSocial.messaging.repository;

import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findAllBySenderAndReceiver(User sender, User receiver);
}
