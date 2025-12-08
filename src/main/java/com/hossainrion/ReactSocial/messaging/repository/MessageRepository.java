package com.hossainrion.ReactSocial.messaging.repository;

import com.hossainrion.ReactSocial.messaging.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

}
