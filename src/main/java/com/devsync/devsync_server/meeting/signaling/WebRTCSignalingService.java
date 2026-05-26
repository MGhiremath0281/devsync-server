package com.devsync.devsync_server.meeting.signaling;

import com.devsync.devsync_server.meeting.dto.MeetingSignalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebRTCSignalingService {

    private final SimpMessagingTemplate messagingTemplate;

    public void handleOffer(MeetingSignalDto signal) {

        log.info(
                "WebRTC OFFER from user {} for meeting {}",
                signal.getSenderId(),
                signal.getMeetingId()
        );

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + signal.getMeetingId() + "/signals",
                signal
        );
    }

    public void handleAnswer(MeetingSignalDto signal) {

        log.info(
                "WebRTC ANSWER from user {} for meeting {}",
                signal.getSenderId(),
                signal.getMeetingId()
        );

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + signal.getMeetingId() + "/signals",
                signal
        );
    }

    public void handleIceCandidate(MeetingSignalDto signal) {

        log.info(
                "ICE Candidate from user {} for meeting {}",
                signal.getSenderId(),
                signal.getMeetingId()
        );

        messagingTemplate.convertAndSend(
                "/topic/meeting/" + signal.getMeetingId() + "/signals",
                signal
        );
    }
}