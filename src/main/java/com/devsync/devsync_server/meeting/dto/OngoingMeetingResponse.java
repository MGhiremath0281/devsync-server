package com.devsync.devsync_server.meeting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OngoingMeetingResponse {

    private Long meetingId;
    private Long channelId;
    private int participantCount;
    private boolean active;
}