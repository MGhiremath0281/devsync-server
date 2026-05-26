package com.devsync.devsync_server.meeting.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meeting_participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetingParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meeting_id", nullable = false)
    private Long meetingId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Builder.Default
    @Column(name = "mic_enabled", nullable = false)
    private boolean micEnabled = true;

    @Builder.Default
    @Column(name = "camera_enabled", nullable = false)
    private boolean cameraEnabled = false;

    @Builder.Default
    @Column(name = "screen_sharing", nullable = false)
    private boolean screenSharing = false;

    @Builder.Default
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();
}