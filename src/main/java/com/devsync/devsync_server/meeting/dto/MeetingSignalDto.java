package com.devsync.devsync_server.meeting.dto;

import lombok.Data;

@Data
public class MeetingSignalDto {

    private Long meetingId;
    private Long senderId;
    private String signalType;
    private Object payload;
}