package com.devsync.devsync_server.message.service;

import com.devsync.devsync_server.message.dto.MessageRequest;
import com.devsync.devsync_server.message.dto.MessageResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MessageService {

    MessageResponse saveChannelMessage(MessageRequest request);

    MessageResponse saveDirectMessage(MessageRequest request);

    List<MessageResponse> getChannelHistory(Long channelId, Pageable pageable);

    List<MessageResponse> getDirectMessageHistory(Long userA, Long userB, Pageable pageable);

    void markAsRead(Long messageId, Long userId);
}