package com.devsync.devsync_server.meeting.dto;

import com.devsync.devsync_server.meeting.model.MeetingType;
import lombok.Data;

@Data
public class CreateMeetingRequest {

    private Long channelId;
    private MeetingType type;
}