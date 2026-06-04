package com.devsync.devsync_server.notification.repository;

import com.devsync.devsync_server.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByUserIdAndReadFalse(Long userId);

    List<Notification> findByUserIdAndReadFalse(Long userId);
}