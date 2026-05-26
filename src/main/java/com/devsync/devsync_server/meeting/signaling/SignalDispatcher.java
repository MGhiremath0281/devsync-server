package com.devsync.devsync_server.meeting.signaling;

import com.devsync.devsync_server.meeting.dto.MeetingSignalDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SignalDispatcher {

    private final WebRTCSignalingService signalingService;

    public void dispatch(MeetingSignalDto signal) {

        switch (signal.getSignalType()) {

            case "OFFER" ->
                    signalingService.handleOffer(signal);

            case "ANSWER" ->
                    signalingService.handleAnswer(signal);

            case "ICE_CANDIDATE" ->
                    signalingService.handleIceCandidate(signal);

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported signal type"
                    );
        }
    }
}