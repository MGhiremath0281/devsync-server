package com.devsync.devsync_server.meeting.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MeetingPresenceService {

    public void userJoined(Long userId, Long meetingId) {
        log.info("User {} joined meeting {}", userId, meetingId);
    }

    public void userLeft(Long userId, Long meetingId) {
        log.info("User {} left meeting {}", userId, meetingId);
    }
}