package com.devsync.devsync_server.meeting.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipantResponse {

    private Long userId;
    private boolean micEnabled;
    private boolean cameraEnabled;
    private boolean screenSharing;
}