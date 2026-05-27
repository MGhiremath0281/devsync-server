package com.devsync.devsync_server.message.controller;

import com.devsync.devsync_server.message.dto.MessageRequest;
import com.devsync.devsync_server.message.dto.MessageResponse;
import com.devsync.devsync_server.message.dto.TypingDto;
import com.devsync.devsync_server.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWSController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendToChannel")
    public void sendToChannel(@Payload MessageRequest request) {

        log.info(
                "WebSocket channel message received | senderId={} | channelId={}",
                request.getSenderId(),
                request.getChannelId()
        );

        try {

            MessageResponse response =
                    messageService.saveChannelMessage(request);

            messagingTemplate.convertAndSend(
                    "/topic/channel/" + request.getChannelId(),
                    response
            );

            log.info(
                    "Channel message broadcasted successfully | messageId={} | channelId={}",
                    response.getId(),
                    request.getChannelId()
            );

        } catch (Exception ex) {

            log.error(
                    "Error processing channel WebSocket message | senderId={} | channelId={}",
                    request.getSenderId(),
                    request.getChannelId(),
                    ex
            );

            throw ex;
        }
    }

    @MessageMapping("/chat.sendToUser")
    public void sendToUser(@Payload MessageRequest request) {

        log.info(
                "WebSocket direct message received | senderId={} | recipientId={}",
                request.getSenderId(),
                request.getRecipientId()
        );

        try {

            MessageResponse response =
                    messageService.saveDirectMessage(request);

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

            log.info(
                    "Direct message delivered successfully | messageId={} | senderId={} | recipientId={}",
                    response.getId(),
                    request.getSenderId(),
                    request.getRecipientId()
            );

        } catch (Exception ex) {

            log.error(
                    "Error processing direct WebSocket message | senderId={} | recipientId={}",
                    request.getSenderId(),
                    request.getRecipientId(),
                    ex
            );

            throw ex;
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(
            @Payload TypingDto typingDto
    ) {

        log.info(
                "Typing indicator received | senderId={} | channelId={} | recipientId={}",
                typingDto.getSenderId(),
                typingDto.getChannelId(),
                typingDto.getRecipientId()
        );

        try {

            if (typingDto.getChannelId() != null) {

                messagingTemplate.convertAndSend(
                        "/topic/channel/"
                                + typingDto.getChannelId()
                                + "/typing",
                        typingDto
                );

                log.info(
                        "Channel typing indicator broadcasted | channelId={}",
                        typingDto.getChannelId()
                );

            } else if (typingDto.getRecipientId() != null) {

                messagingTemplate.convertAndSendToUser(
                        typingDto.getRecipientId().toString(),
                        "/queue/typing",
                        typingDto
                );

                log.info(
                        "Direct typing indicator sent | recipientId={}",
                        typingDto.getRecipientId()
                );
            }

        } catch (Exception ex) {

            log.error(
                    "Error processing typing indicator",
                    ex
            );

            throw ex;
        }
    }
}