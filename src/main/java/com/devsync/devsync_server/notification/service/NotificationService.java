package com.devsync.devsync_server.notification.service;

import com.devsync.devsync_server.notification.dto.CreateNotificationRequest;
import com.devsync.devsync_server.notification.dto.NotificationResponse;
import com.devsync.devsync_server.notification.mapper.NotificationMapper;
import com.devsync.devsync_server.notification.model.Notification;
import com.devsync.devsync_server.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        log.info("Creating notification for userId={}, title={}", request.getUserId(), request.getTitle());

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .message(request.getMessage())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification created successfully. notificationId={}", saved.getId());
        return notificationMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId) {
        log.info("Fetching all notifications for userId={}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        long count = notificationRepository.countByUserIdAndReadFalse(userId);
        log.info("Unread notification count for userId={} is {}", userId, count);
        return count;
    }

    @Transactional
    public void markAsRead(Long userId, Long notificationId) {
        log.info("Attempting to mark notificationId={} as read for userId={}", notificationId, userId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.warn("Notification not found. notificationId={}", notificationId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
                });

        if (!notification.getUserId().equals(userId)) {
            log.warn("Unauthorized markAsRead attempt. userId={} does not own notificationId={}", userId, notificationId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
        log.info("NotificationId={} marked as read successfully", notificationId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for userId={}", userId);
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndReadFalse(userId);

        for (Notification notification : unreadNotifications) {
            notification.setRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
        log.info("Successfully marked {} notifications as read for userId={}", unreadNotifications.size(), userId);
    }

    @Transactional
    public void deleteNotification(Long userId, Long notificationId) {
        log.info("Attempting to delete notificationId={} for userId={}", notificationId, userId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> {
                    log.warn("Notification not found for deletion. notificationId={}", notificationId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
                });

        if (!notification.getUserId().equals(userId)) {
            log.warn("Unauthorized deletion attempt. userId={} tried to delete notificationId={}", userId, notificationId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized access to notification");
        }

        notificationRepository.delete(notification);
        log.info("NotificationId={} deleted successfully", notificationId);
    }
}