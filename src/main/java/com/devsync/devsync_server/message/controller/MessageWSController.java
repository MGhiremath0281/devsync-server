package com.devsync.devsync_server.message.controller;

import com.devsync.devsync_server.message.dto.MessageRequest;
import com.devsync.devsync_server.message.dto.MessageResponse;
import com.devsync.devsync_server.message.dto.TypingDto;
import com.devsync.devsync_server.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWSController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendToChannel")
    public void sendToChannel(@Payload MessageRequest request) {
        MessageResponse response = messageService.saveChannelMessage(request);

        messagingTemplate.convertAndSend("/topic/channel/" + request.getChannelId(), response);
    }

    @MessageMapping("/chat.sendToUser")
    public void sendToUser(@Payload MessageRequest request) {
        MessageResponse response = messageService.saveDirectMessage(request);
        messagingTemplate.convertAndSendToUser(
                request.getRecipientId().toString(),
                "/queue/messages",
                response
        );

        messagingTemplate.convertAndSendToUser(
                request.getSenderId().toString(),
                "/queue/messages",
                response
        );
    }

    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(@Payload TypingDto typingDto) {
        if (typingDto.getChannelId() != null) {
            messagingTemplate.convertAndSend("/topic/channel/" + typingDto.getChannelId() + "/typing", typingDto);
        } else if (typingDto.getRecipientId() != null) {
            messagingTemplate.convertAndSendToUser(
                    typingDto.getRecipientId().toString(),
                    "/queue/typing",
                    typingDto
            );
        }
    }
}