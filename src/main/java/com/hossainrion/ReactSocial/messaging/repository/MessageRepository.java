package com.hossainrion.ReactSocial.messaging.repository;

import com.hossainrion.ReactSocial.entity.User;
import com.hossainrion.ReactSocial.messaging.entity.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

    List<Message> findAllBySenderAndReceiver(User sender, User receiver);

    @Transactional
    @Modifying
    @Query(value = "UPDATE message SET seen=1 WHERE sender_id=:senderId AND receiver_id=:receiverId", nativeQuery = true)
    void setSeenBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @Query(value = "SELECT DISTINCT ON (other_user) * FROM (SELECT CASE WHEN receiver_id=:myId THEN sender_id ELSE receiver_id END as other_user, * FROM message WHERE sender_id=:myId OR receiver_id=:myId) ORDER BY other_user, id DESC", nativeQuery = true)
    List<Message> findAllForMessgePage(Long myId);

    Integer countBySeenEqualsAndReceiverEqualsAndSenderEquals(int i, User receiver, User sender);
}
