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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ReceiptRepository receiptRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public MessageResponse saveChannelMessage(MessageRequest request) {

        log.info(
                "Saving channel message | senderId={} | channelId={} | type={}",
                request.getSenderId(),
                request.getChannelId(),
                request.getType()
        );

        if (request.getChannelId() == null) {

            log.error(
                    "Channel message failed: channelId is null | senderId={}",
                    request.getSenderId()
            );

            throw new IllegalArgumentException(
                    "Channel ID cannot be null for channel messages"
            );
        }

        try {

            Channel channelProxy =
                    entityManager.getReference(
                            Channel.class,
                            request.getChannelId()
                    );

            Message message = Message.builder()
                    .content(request.getContent())
                    .type(
                            MessageType.valueOf(
                                    request.getType().toUpperCase()
                            )
                    )
                    .codeLanguage(request.getCodeLanguage())
                    .senderId(request.getSenderId())
                    .channel(channelProxy)
                    .build();

            Message saved = messageRepository.save(message);

            log.info(
                    "Channel message saved successfully | messageId={} | channelId={}",
                    saved.getId(),
                    request.getChannelId()
            );

            return mapToResponse(saved);

        } catch (Exception ex) {

            log.error(
                    "Error while saving channel message | senderId={} | channelId={}",
                    request.getSenderId(),
                    request.getChannelId(),
                    ex
            );

            throw ex;
        }
    }

    @Override
    @Transactional
    public MessageResponse saveDirectMessage(MessageRequest request) {

        log.info(
                "Saving direct message | senderId={} | recipientId={}",
                request.getSenderId(),
                request.getRecipientId()
        );

        if (request.getRecipientId() == null) {

            log.error(
                    "Direct message failed: recipientId is null | senderId={}",
                    request.getSenderId()
            );

            throw new IllegalArgumentException(
                    "Recipient ID cannot be null for direct messages"
            );
        }

        try {

            Message message = Message.builder()
                    .content(request.getContent())
                    .type(
                            MessageType.valueOf(
                                    request.getType().toUpperCase()
                            )
                    )
                    .codeLanguage(request.getCodeLanguage())
                    .senderId(request.getSenderId())
                    .recipientId(request.getRecipientId())
                    .build();

            Message saved = messageRepository.save(message);

            log.info(
                    "Direct message saved successfully | messageId={}",
                    saved.getId()
            );

            return mapToResponse(saved);

        } catch (Exception ex) {

            log.error(
                    "Error while saving direct message | senderId={} | recipientId={}",
                    request.getSenderId(),
                    request.getRecipientId(),
                    ex
            );

            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getChannelHistory(
            Long channelId,
            Pageable pageable
    ) {

        log.info(
                "Fetching channel history | channelId={} | pageSize={}",
                channelId,
                pageable.getPageSize()
        );

        try {

            Slice<Message> messageSlice =
                    messageRepository.findByChannel_IdOrderByCreatedAtDesc(
                            channelId,
                            pageable
                    );

            log.info(
                    "Fetched {} messages from channel {}",
                    messageSlice.getNumberOfElements(),
                    channelId
            );

            return messageSlice.getContent()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

        } catch (Exception ex) {

            log.error(
                    "Error fetching channel history | channelId={}",
                    channelId,
                    ex
            );

            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getDirectMessageHistory(
            Long userA,
            Long userB,
            Pageable pageable
    ) {

        log.info(
                "Fetching DM history | userA={} | userB={}",
                userA,
                userB
        );

        try {

            Slice<Message> messageSlice =
                    messageRepository.findDirectMessages(
                            userA,
                            userB,
                            pageable
                    );

            log.info(
                    "Fetched {} direct messages between users {} and {}",
                    messageSlice.getNumberOfElements(),
                    userA,
                    userB
            );

            return messageSlice.getContent()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

        } catch (Exception ex) {

            log.error(
                    "Error fetching direct message history | userA={} | userB={}",
                    userA,
                    userB,
                    ex
            );

            throw ex;
        }
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId, Long userId) {

        log.info(
                "Marking message as read | messageId={} | userId={}",
                messageId,
                userId
        );

        try {

            boolean alreadyRead =
                    receiptRepository
                            .findByMessageIdAndUserId(messageId, userId)
                            .isPresent();

            if (alreadyRead) {

                log.warn(
                        "Message already marked as read | messageId={} | userId={}",
                        messageId,
                        userId
                );

                return;
            }

            MessageReceipt receipt = MessageReceipt.builder()
                    .messageId(messageId)
                    .userId(userId)
                    .build();

            receiptRepository.save(receipt);

            log.info(
                    "Read receipt saved | messageId={} | userId={}",
                    messageId,
                    userId
            );

        } catch (Exception ex) {

            log.error(
                    "Error marking message as read | messageId={} | userId={}",
                    messageId,
                    userId,
                    ex
            );

            throw ex;
        }
    }

    private MessageResponse mapToResponse(Message message) {

        return MessageResponse.builder()
                .id(message.getId())
                .content(message.getContent())
                .type(message.getType().name())
                .codeLanguage(message.getCodeLanguage())
                .senderId(message.getSenderId())
                .channelId(
                        message.getChannel() != null
                                ? message.getChannel().getId()
                                : null
                )
                .recipientId(message.getRecipientId())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .build();
    }
}