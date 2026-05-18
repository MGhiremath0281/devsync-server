package com.devsync.devsync_server.message.service.impl;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ReceiptRepository receiptRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public MessageResponse saveChannelMessage(MessageRequest request) {
        if (request.getChannelId() == null) {
            throw new IllegalArgumentException("Channel ID cannot be null for channel messages");
        }

        Channel channelProxy = entityManager.getReference(Channel.class, request.getChannelId());

        Message message = Message.builder()
                .content(request.getContent())
                .type(MessageType.valueOf(request.getType().toUpperCase()))
                .codeLanguage(request.getCodeLanguage())
                .senderId(request.getSenderId())
                .channel(channelProxy)
                .build();

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MessageResponse saveDirectMessage(MessageRequest request) {
        if (request.getRecipientId() == null) {
            throw new IllegalArgumentException("Recipient ID cannot be null for direct messages");
        }

        Message message = Message.builder()
                .content(request.getContent())
                .type(MessageType.valueOf(request.getType().toUpperCase()))
                .codeLanguage(request.getCodeLanguage())
                .senderId(request.getSenderId())
                .recipientId(request.getRecipientId())
                .build();

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getChannelHistory(Long channelId, Pageable pageable) {
        Slice<Message> messageSlice = messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, pageable);
        return messageSlice.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getDirectMessageHistory(Long userA, Long userB, Pageable pageable) {
        Slice<Message> messageSlice = messageRepository.findDirectMessages(userA, userB, pageable);
        return messageSlice.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId, Long userId) {
        if (receiptRepository.findByMessageIdAndUserId(messageId, userId).isEmpty()) {
            MessageReceipt receipt = MessageReceipt.builder()
                    .messageId(messageId)
                    .userId(userId)
                    .build();
            receiptRepository.save(receipt);
        }
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getType().name())
                .codeLanguage(message.getCodeLanguage())
                .senderId(message.getSenderId())
                .channelId(message.getChannel() != null ? message.getChannel().getId() : null)
                .recipientId(message.getRecipientId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}
