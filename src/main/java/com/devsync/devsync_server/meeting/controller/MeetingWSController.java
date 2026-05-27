package com.devsync.devsync_server.meeting.controller;

import com.devsync.devsync_server.meeting.dto.MeetingSignalDto;
import com.devsync.devsync_server.meeting.signaling.SignalDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MeetingWSController {

    private final SignalDispatcher signalDispatcher;

    @MessageMapping("/meeting.signal")
    public void handleSignal(
            @Payload MeetingSignalDto signal
    ) {

        log.info(
                "Received signal type {} for meeting {}",
                signal.getSignalType(),
                signal.getMeetingId()
        );

        signalDispatcher.dispatch(signal);
    }
}