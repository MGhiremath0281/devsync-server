package com.devsync.devsync_server.meeting.repository;

import com.devsync.devsync_server.meeting.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Optional<Meeting> findByChannelIdAndActiveTrue(Long channelId);

    List<Meeting> findByActiveTrue();
}