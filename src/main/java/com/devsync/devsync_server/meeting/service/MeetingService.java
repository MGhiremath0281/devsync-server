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

        if (!teamService.isUserMember(userId, teamId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied to workspace"
            );
        }

        meetingRepository.findByChannelIdAndActiveTrue(channelId)
                .ifPresent(existing -> {
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

        addParticipant(savedMeeting.getId(), userId);

        log.info(
                "Meeting {} created in channel {} by user {}",
                savedMeeting.getId(),
                channelId,
                userId
        );

        MeetingResponse response = mapToResponse(savedMeeting);

        eventPublisher.publishMeetingCreated(response);

        return response;
    }

    public MeetingResponse joinMeeting(
            Long userId,
            Long meetingId
    ) {

        Meeting meeting = getActiveMeeting(meetingId);

        participantRepository.findByMeetingIdAndUserId(
                meetingId,
                userId
        ).orElseGet(() -> addParticipant(meetingId, userId));

        log.info(
                "User {} joined meeting {}",
                userId,
                meetingId
        );

        MeetingResponse response = mapToResponse(meeting);

        eventPublisher.publishParticipantJoined(
                meetingId,
                response
        );

        return response;
    }

    public void leaveMeeting(
            Long userId,
            Long meetingId
    ) {

        Meeting meeting = getActiveMeeting(meetingId);

        MeetingParticipant participant = participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Participant not found"
                ));

        participantRepository.delete(participant);

        log.info(
                "User {} left meeting {}",
                userId,
                meetingId
        );

        long remainingParticipants =
                participantRepository.countByMeetingId(meetingId);

        if (remainingParticipants == 0) {

            meeting.setActive(false);
            meeting.setStatus(MeetingStatus.ENDED);
            meeting.setEndedAt(LocalDateTime.now());

            meetingRepository.save(meeting);

            eventPublisher.publishMeetingEnded(meetingId);

            log.info("Meeting {} ended", meetingId);

            return;
        }

        eventPublisher.publishParticipantLeft(
                meetingId,
                userId
        );
    }

    public List<MeetingResponse> getOngoingMeetings() {

        return meetingRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MeetingResponse toggleMic(
            Long userId,
            Long meetingId,
            boolean enabled
    ) {

        MeetingParticipant participant = getParticipant(
                meetingId,
                userId
        );

        participant.setMicEnabled(enabled);

        participantRepository.save(participant);

        MeetingResponse response =
                mapToResponse(getActiveMeeting(meetingId));

        eventPublisher.publishMicStateChanged(
                meetingId,
                response
        );

        return response;
    }

    public MeetingResponse toggleCamera(
            Long userId,
            Long meetingId,
            boolean enabled
    ) {

        MeetingParticipant participant = getParticipant(
                meetingId,
                userId
        );

        participant.setCameraEnabled(enabled);

        participantRepository.save(participant);

        MeetingResponse response =
                mapToResponse(getActiveMeeting(meetingId));

        eventPublisher.publishCameraStateChanged(
                meetingId,
                response
        );

        return response;
    }

    public MeetingResponse toggleScreenShare(
            Long userId,
            Long meetingId,
            boolean enabled
    ) {

        MeetingParticipant participant = getParticipant(
                meetingId,
                userId
        );

        participant.setScreenSharing(enabled);

        participantRepository.save(participant);

        MeetingResponse response =
                mapToResponse(getActiveMeeting(meetingId));

        eventPublisher.publishScreenShareChanged(
                meetingId,
                response
        );

        return response;
    }

    private MeetingParticipant addParticipant(
            Long meetingId,
            Long userId
    ) {

        MeetingParticipant participant =
                MeetingParticipant.builder()
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

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Meeting not found"
                ));

        if (!meeting.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Meeting already ended"
            );
        }

        return meeting;
    }

    private MeetingParticipant getParticipant(
            Long meetingId,
            Long userId
    ) {

        return participantRepository
                .findByMeetingIdAndUserId(meetingId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Participant not found"
                ));
    }

    private MeetingResponse mapToResponse(Meeting meeting) {

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