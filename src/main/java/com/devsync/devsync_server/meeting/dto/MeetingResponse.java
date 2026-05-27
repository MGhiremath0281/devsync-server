package com.devsync.devsync_server.meeting.dto;

import com.devsync.devsync_server.meeting.model.MeetingStatus;
import com.devsync.devsync_server.meeting.model.MeetingType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class MeetingResponse {

    private Long meetingId;
    private Long channelId;
    private Long createdBy;
    private MeetingType type;
    private MeetingStatus status;
    private boolean active;
    private LocalDateTime startedAt;
    private List<ParticipantResponse> participants;
}