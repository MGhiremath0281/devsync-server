package com.devsync.devsync_server.message.repository;

import com.devsync.devsync_server.message.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Slice<Message> findByChannelIdOrderByCreatedAtDesc(Long channelId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userA AND m.recipientId = :userB) OR " +
            "(m.senderId = :userB AND m.recipientId = :userA) " +
            "ORDER BY m.createdAt DESC")
    Slice<Message> findDirectMessages(
            @Param("userA") Long userA,
            @Param("userB") Long userB,
            Pageable pageable
    );
}