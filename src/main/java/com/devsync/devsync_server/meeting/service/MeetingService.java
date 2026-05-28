package com.devsync.devsync_server.meeting.service;

import com.devsync.devsync_server.meeting.dto.MeetingResponse;
import com.devsync.devsync_server.meeting.dto.ParticipantResponse;
import com.devsync.devsync_server.meeting.model.Meeting;
import com.devsync.devsync_server.meeting.model.MeetingParticipant;
import com.devsync.devsync_server.meeting.model.MeetingStatus;
import com.devsync.devsync_server.meeting.model.MeetingType;
import com.devsync.devsync_server.meeting.repository.MeetingParticipantRepository;
import com.devsync.devsync_server.meeting.repository.MeetingRepository;
import com.devsync.devsync_server.meeting.websocket.MeetingEventPublisher;
import com.devsync.devsync_server.workspace.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository participantRepository;
    private final TeamService teamService;
    private final MeetingEventPublisher eventPublisher;

    public MeetingResponse createMeeting(
            Long userId,
            Long teamId,
            Long channelId,
            MeetingType type
    ) {
        log.info("Attempting to create meeting of type [{}] in channel [{}] by user [{}]", type, channelId, userId);

        if (!teamService.isUserMember(userId, teamId)) {
            log.warn("Meeting creation rejected: User [{}] is not a member of team [{}]", userId, teamId);
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied to workspace"
            );
        }

        meetingRepository.findByChannelIdAndActiveTrue(channelId)
                .ifPresent(existing -> {
                    log.warn("Meeting creation rejected: Channel [{}] already has an active meeting [{}]", channelId, existing.getId());
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Meeting already active in this channel"
                    );
                });

        Meeting meeting = Meeting.builder()
                .channelId(channelId)
                .createdBy(userId)
                .type(type)
                .status(MeetingStatus.ACTIVE)
                .active(true)
                .startedAt(LocalDateTime.now())
                .build();

        Meeting savedMeeting = meetingRepository.save(meeting);
        log.debug("Saved initial meeting entity with ID [{}]", savedMeeting.getId());

        addParticipant(savedMeeting.getId(), userId);

        log.info("Meeting [{}] successfully created in channel [{}] by user [{}]", savedMeeting.getId(), channelId, userId);

        MeetingResponse response = mapToResponse(savedMeeting);

        log.debug("Publishing meeting creation event over WebSocket for meeting [{}]", savedMeeting.getId());
        eventPublisher.publishMeetingCreated(response);

        return response;
    }

    public MeetingResponse joinMeeting(
            Long userId,
            Long meetingId
    ) {
        log.info("User [{}] attempting to join meeting [{}]", userId, meetingId);

        Meeting meeting = getActiveMeeting(meetingId);

        participantRepository.findByMeetingIdAndUserId(meetingId, userId)
                .ifPresentOrElse(
                        existing -> log.debug("User [{}] is already a participant in meeting [{}]", userId, meetingId),
                        () -> {
                            addParticipant(meetingId, userId);
                            log.debug("Added new participant record for user [{}] in meeting [{}]", userId, meetingId);
                        }
                );

        log.info("User [{}] successfully joined meeting [{}]", userId, meetingId);

        MeetingResponse response = mapToResponse(meeting);

        log.debug("Publishing participant joined event over WebSocket for meeting [{}]", meetingId);
        eventPublisher.publishParticipantJoined(meetingId, response);

        return response;
    }

    public void leaveMeeting(
            Long userId,
            Long meetingId
    ) {
        log.info("User [{}] attempting to leave meeting [{}]", userId, meetingId);

        Meeting meeting = getActiveMeeting(meetingId);

        MeetingParticipant participant = participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> {
                    log.warn("Leave meeting failed: User [{}] is not a participant in meeting [{}]", userId, meetingId);
                    return new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Participant not found"
                    );
                });

        participantRepository.delete(participant);
        log.debug("Deleted participant record for user [{}] in meeting [{}]", userId, meetingId);

        long remainingParticipants = participantRepository.countByMeetingId(meetingId);
        log.debug("Remaining participant count for meeting [{}]: {}", meetingId, remainingParticipants);

        if (remainingParticipants == 0) {
            meeting.setActive(false);
            meeting.setStatus(MeetingStatus.ENDED);
            meeting.setEndedAt(LocalDateTime.now());

            meetingRepository.save(meeting);
            log.info("Meeting [{}] has no participants remaining. Meeting ended.", meetingId);

            log.debug("Publishing meeting ended event over WebSocket for meeting [{}]", meetingId);
            eventPublisher.publishMeetingEnded(meetingId);
            return;
        }

        log.debug("Publishing participant left event over WebSocket for user [{}] in meeting [{}]", userId, meetingId);
        eventPublisher.publishParticipantLeft(meetingId, userId);
    }

    public List<MeetingResponse> getOngoingMeetings() {
        log.debug("Fetching all ongoing active meetings");
        List<Meeting> ongoing = meetingRepository.findByActiveTrue();
        log.info("Found {} ongoing meeting(s)", ongoing.size());

        return ongoing.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MeetingResponse toggleMic(
            Long userId,
            Long meetingId,
            boolean enabled
    ) {
        log.info("User [{}] toggling mic to [{}] in meeting [{}]", userId, enabled, meetingId);

        MeetingParticipant participant = getParticipant(meetingId, userId);
        participant.setMicEnabled(enabled);
        participantRepository.save(participant);

        MeetingResponse response = mapToResponse(getActiveMeeting(meetingId));

        log.debug("Publishing mic state change event over WebSocket for meeting [{}]", meetingId);
        eventPublisher.publishMicStateChanged(meetingId, response);

        return response;
    }

    public MeetingResponse toggleCamera(
            Long userId,
            Long meetingId,
            boolean enabled
    ) {
        log.info("User [{}] toggling camera to [{}] in meeting [{}]", userId, enabled, meetingId);

        MeetingParticipant participant = getParticipant(meetingId, userId);
        participant.setCameraEnabled(enabled);
        participantRepository.save(participant);

        MeetingResponse response = mapToResponse(getActiveMeeting(meetingId));

        log.debug("Publishing camera state change event over WebSocket for meeting [{}]", meetingId);
        eventPublisher.publishCameraStateChanged(meetingId, response);

        return response;
    }

    public MeetingResponse toggleScreenShare(
            Long userId,
            Long meetingId,
            boolean enabled
    ) {
        log.info("User [{}] toggling screen share to [{}] in meeting [{}]", userId, enabled, meetingId);

        MeetingParticipant participant = getParticipant(meetingId, userId);
        participant.setScreenSharing(enabled);
        participantRepository.save(participant);

        MeetingResponse response = mapToResponse(getActiveMeeting(meetingId));

        log.debug("Publishing screen share state change event over WebSocket for meeting [{}]", meetingId);
        eventPublisher.publishScreenShareChanged(meetingId, response);

        return response;
    }

    private MeetingParticipant addParticipant(
            Long meetingId,
            Long userId
    ) {
        log.debug("Creating participant record for user [{}] in meeting [{}]", userId, meetingId);
        MeetingParticipant participant = MeetingParticipant.builder()
                .meetingId(meetingId)
                .userId(userId)
                .micEnabled(true)
                .cameraEnabled(false)
                .screenSharing(false)
                .joinedAt(LocalDateTime.now())
                .build();

        return participantRepository.save(participant);
    }

    private Meeting getActiveMeeting(Long meetingId) {
        return meetingRepository.findById(meetingId)
                .map(meeting -> {
                    if (!meeting.isActive()) {
                        log.warn("Lookup failed: Meeting [{}] found but is already inactive", meetingId);
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Meeting already ended"
                        );
                    }
                    return meeting;
                })
                .orElseThrow(() -> {
                    log.warn("Lookup failed: Meeting [{}] does not exist", meetingId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Meeting not found"
                    );
                });
    }

    private MeetingParticipant getParticipant(
            Long meetingId,
            Long userId
    ) {
        return participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> {
                    log.warn("Participant lookup failed: User [{}] is not in meeting [{}]", userId, meetingId);
                    return new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Participant not found"
                    );
                });
    }

    private MeetingResponse mapToResponse(Meeting meeting) {
        // Keeping this on trace/debug level as mapping runs frequently
        log.trace("Mapping meeting entity [{}] to response DTO", meeting.getId());

        List<ParticipantResponse> participants =
                participantRepository
                        .findByMeetingId(meeting.getId())
                        .stream()
                        .map(participant ->
                                ParticipantResponse.builder()
                                        .userId(participant.getUserId())
                                        .micEnabled(participant.isMicEnabled())
                                        .cameraEnabled(participant.isCameraEnabled())
                                        .screenSharing(participant.isScreenSharing())
                                        .build()
                        )
                        .toList();

        return MeetingResponse.builder()
                .meetingId(meeting.getId())
                .channelId(meeting.getChannelId())
                .createdBy(meeting.getCreatedBy())
                .type(meeting.getType())
                .status(meeting.getStatus())
                .active(meeting.isActive())
                .startedAt(meeting.getStartedAt())
                .participants(participants)
                .build();
    }
}