package com.devsync.devsync_server.meeting.service;

import com.devsync.devsync_server.meeting.model.MeetingParticipant;
import com.devsync.devsync_server.meeting.repository.MeetingParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingParticipantService {

    private final MeetingParticipantRepository participantRepository;

    public MeetingParticipant addParticipant(Long meetingId, Long userId) {

        return participantRepository.save(
                MeetingParticipant.builder()
                        .meetingId(meetingId)
                        .userId(userId)
                        .build()
        );
    }

    public List<MeetingParticipant> getParticipants(Long meetingId) {
        return participantRepository.findByMeetingId(meetingId);
    }

    public long countParticipants(Long meetingId) {
        return participantRepository.countByMeetingId(meetingId);
    }
}