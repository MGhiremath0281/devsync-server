package com.devsync.devsync_server.message.service.impl;

import com.devsync.devsync_server.auth.entity.User;
import com.devsync.devsync_server.auth.repository.UserRepository;
import com.devsync.devsync_server.message.channel.Channel;
import com.devsync.devsync_server.message.dto.MessageRequest;
import com.devsync.devsync_server.message.dto.MessageResponse;
import com.devsync.devsync_server.message.model.Message;
import com.devsync.devsync_server.message.model.MessageReceipt;
import com.devsync.devsync_server.message.model.MessageType;
import com.devsync.devsync_server.message.repository.MessageRepository;
import com.devsync.devsync_server.message.repository.ReceiptRepository;
import com.devsync.devsync_server.message.service.MessageService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ReceiptRepository receiptRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public MessageResponse saveChannelMessage(MessageRequest request) {
        log.info("Saving channel message | senderId={} | channelId={} | type={}",
                request.getSenderId(), request.getChannelId(), request.getType());

        if (request.getChannelId() == null) {
            throw new IllegalArgumentException("Channel ID cannot be null.");
        }

        MessageType messageType = MessageType.valueOf(request.getType().toUpperCase());
        validateMessagePayload(request, messageType);

        try {
            Channel channelProxy = entityManager.getReference(Channel.class, request.getChannelId());

            Message message = Message.builder()
                    .content(request.getContent())
                    .type(messageType)
                    .codeLanguage(request.getCodeLanguage())
                    .senderId(request.getSenderId())
                    .channel(channelProxy)
                    .build();

            Message saved = messageRepository.save(message);
            log.info("Channel message saved successfully | messageId={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception ex) {
            log.error("Failed saving channel message", ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public MessageResponse saveDirectMessage(MessageRequest request) {
        log.info("Saving direct message | senderId={} | recipientId={}",
                request.getSenderId(), request.getRecipientId());

        if (request.getRecipientId() == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null.");
        }

        MessageType messageType = MessageType.valueOf(request.getType().toUpperCase());
        validateMessagePayload(request, messageType);

        try {
            Message message = Message.builder()
                    .content(request.getContent())
                    .type(messageType)
                    .codeLanguage(request.getCodeLanguage())
                    .senderId(request.getSenderId())
                    .recipientId(request.getRecipientId())
                    .build();

            Message saved = messageRepository.save(message);
            log.info("Direct message saved successfully | messageId={}", saved.getId());
            return mapToResponse(saved);
        } catch (Exception ex) {
            log.error("Failed saving direct message", ex);
            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getChannelHistory(Long channelId, Pageable pageable) {
        Slice<Message> messageSlice = messageRepository.findByChannel_IdOrderByCreatedAtDesc(channelId, pageable);
        return messageSlice.getContent().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getDirectMessageHistory(Long userA, Long userB, Pageable pageable) {
        Slice<Message> messageSlice = messageRepository.findDirectMessages(userA, userB, pageable);
        return messageSlice.getContent().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        boolean alreadyRead = receiptRepository.findByMessageIdAndUserId(messageId, userId).isPresent();

        if (alreadyRead) {
            log.warn("Message already marked as read | messageId={} | userId={}", messageId, userId);
            return;
        }

        MessageReceipt receipt = MessageReceipt.builder()
                .messageId(messageId)
                .userId(userId)
                .build();

        receiptRepository.save(receipt);
        log.info("Read receipt saved | messageId={} | userId={}", messageId, userId);
    }

    /**
     * Validate advanced message payloads
     */
    private void validateMessagePayload(MessageRequest request, MessageType messageType) {
        // CODE messages require language
        if (MessageType.CODE.equals(messageType) &&
                (request.getCodeLanguage() == null || request.getCodeLanguage().isBlank())) {
            throw new IllegalArgumentException("Code language required for CODE messages.");
        }

        // FILE messages require attachments
        if (MessageType.FILE.equals(messageType) &&
                (request.getAttachments() == null || request.getAttachments().isEmpty())) {
            throw new IllegalArgumentException("Attachments required for FILE messages.");
        }
    }

    private MessageResponse mapToResponse(Message message) {
        // Attempt to fetch the user from the database
        User sender = userRepository.findById(message.getSenderId()).orElse(null);

        // Fallback: If user is not found, use a descriptive identifier (e.g., "User 1")
        String displayName = (sender != null)
                ? sender.getUsername()
                : "User " + message.getSenderId();

        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getType().name())
                .codeLanguage(message.getCodeLanguage())
                .senderId(message.getSenderId())
                .senderName(displayName)
                .senderAvatar(null)
                .channelId(message.getChannel() != null ? message.getChannel().getId() : null)
                .recipientId(message.getRecipientId())
                .edited(false)
                .replyToMessageId(null)
                .attachments(Collections.emptyList())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}