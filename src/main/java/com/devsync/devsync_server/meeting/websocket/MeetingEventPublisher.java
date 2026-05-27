package com.devsync.devsync_server.meeting.websocket;

import com.devsync.devsync_server.meeting.dto.MeetingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishMeetingCreated(
            MeetingResponse response
    ) {

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + response.getMeetingId(),
                response
        );
    }

    public void publishParticipantJoined(
            Long meetingId,
            MeetingResponse response
    ) {

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + meetingId,
                response
        );
    }

    public void publishParticipantLeft(
            Long meetingId,
            Long userId
    ) {

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + meetingId + "/leave",
                userId
        );
    }

    public void publishMeetingEnded(
            Long meetingId
    ) {

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + meetingId + "/ended",
                "MEETING_ENDED"
        );
    }

    public void publishMicStateChanged(
            Long meetingId,
            MeetingResponse response
    ) {

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + meetingId + "/mic",
                response
        );
    }

    public void publishCameraStateChanged(
            Long meetingId,
            MeetingResponse response
    ) {

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + meetingId + "/camera",
                response
        );
    }

    public void publishScreenShareChanged(
            Long meetingId,
            MeetingResponse response
    ) {

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + meetingId + "/screen-share",
                response
        );
    }
}