package com.devsync.devsync_server.meeting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
@Slf4j
public class MeetingPresenceService {

    public void userJoined(Long userId, Long meetingId) {
        log.debug("Processing join request - User: {}, Meeting: {}", userId, meetingId);

        try {

            log.info("Successfully registered join event - User [{}] entered Meeting [{}]", userId, meetingId);
        } catch (Exception e) {
            log.error("Failed to process join event for User [{}] in Meeting [{}]. Error: {}",
                    userId, meetingId, e.getMessage(), e);
            throw e;
        }
    }

    public void userLeft(Long userId, Long meetingId) {
        log.debug("Processing leave request - User: {}, Meeting: {}", userId, meetingId);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            stopWatch.stop();
            log.info("Successfully registered leave event - User [{}] left Meeting [{}] (Processed in {}ms)",
                    userId, meetingId, stopWatch.getTotalTimeMillis());
        } catch (Exception e) {
            log.error("Failed to process leave event for User [{}] in Meeting [{}]. Error: {}",
                    userId, meetingId, e.getMessage(), e);
            throw e;
        }
    }
}