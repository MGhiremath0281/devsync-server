package com.devsync.devsync_server.meeting.signaling;

import com.devsync.devsync_server.meeting.dto.MeetingSignalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignalDispatcher {

    private final WebRTCSignalingService signalingService;

    public void dispatch(MeetingSignalDto signal) {

        switch (signal.getSignalType()) {
            case "JOIN" ->
                    signalingService.handleJoin(signal);

            case "OFFER" ->
                    signalingService.handleOffer(signal);

            case "ANSWER" ->
                    signalingService.handleAnswer(signal);

            case "ICE_CANDIDATE" ->
                    signalingService.handleIceCandidate(signal);

            default -> {
                log.error("Rejecting unmapped signal type action: [{}]", signal.getSignalType());
                throw new IllegalArgumentException(
                        "Unsupported signal type: " + signal.getSignalType()
                );
            }
        }
    }
}