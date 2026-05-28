package com.devsync.devsync_server.meeting.service;

import com.devsync.devsync_server.meeting.model.MeetingParticipant;
import com.devsync.devsync_server.meeting.repository.MeetingParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingParticipantService {

    private final MeetingParticipantRepository participantRepository;

    public MeetingParticipant addParticipant(Long meetingId, Long userId) {
        log.info("Attempting to add user ID: {} to meeting ID: {}", userId, meetingId);

        try {
            MeetingParticipant savedParticipant = participantRepository.save(
                    MeetingParticipant.builder()
                            .meetingId(meetingId)
                            .userId(userId)
                            .build()
            );
            log.info("Successfully added user ID: {} to meeting ID: {} with participant ID: {}",
                    userId, meetingId, savedParticipant.getId()); // Assumes getId() exists on your model
            return savedParticipant;
        } catch (Exception e) {
            log.error("Failed to add user ID: {} to meeting ID: {}. Error: {}", userId, meetingId, e.getMessage(), e);
            throw e;
        }
    }

    public List<MeetingParticipant> getParticipants(Long meetingId) {
        log.debug("Fetching participants for meeting ID: {}", meetingId);

        List<MeetingParticipant> participants = participantRepository.findByMeetingId(meetingId);

        log.info("Found {} participants for meeting ID: {}", participants.size(), meetingId);
        return participants;
    }

    public long countParticipants(Long meetingId) {
        log.debug("Counting participants for meeting ID: {}", meetingId);

        long count = participantRepository.countByMeetingId(meetingId);

        log.info("Total participant count for meeting ID: {} is {}", meetingId, count);
        return count;
    }
}